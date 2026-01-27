package com.yuno.payments.example.features.payment.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yuno.payments.example.BuildConfig
import com.yuno.payments.example.R
import com.yuno.payments.example.ui.views.CustomEditText
import com.yuno.payments.features.payment.models.OneTimeTokenModel
import com.yuno.presentation.core.components.PaymentSelected
import com.yuno.sdk.payments.render.YunoPaymentFragmentController
import com.yuno.sdk.payments.render.YunoPaymentRenderListener
import com.yuno.sdk.payments.render.startPaymentRender
import com.yuno.sdk.payments.startCheckout
import com.yuno.sdk.payments.updateCheckoutSession

class PaymentRenderActivity : AppCompatActivity(), YunoPaymentRenderListener {

    private lateinit var fragmentController: YunoPaymentFragmentController

    private lateinit var editTextCheckoutSession: CustomEditText
    private lateinit var editTextCountryCode: CustomEditText
    private lateinit var editTextPaymentMethod: CustomEditText
    private lateinit var buttonStartPayment: Button
    private lateinit var buttonSubmitPayment: Button
    private lateinit var buttonContinuePayment: Button
    private lateinit var textViewOtt: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var configurationContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_render)

        // IMPORTANT: startCheckout() must be called in onCreate() before using any payment render functions.
        // This initializes the payment flow and injects necessary dependencies for the SDK to work properly.
        // Without this call, you'll get "Object not injected" errors when calling startPaymentRender().
        startCheckout()

        initViews()
        initListeners()
    }

    private fun initViews() {
        editTextCheckoutSession = findViewById(R.id.editText_checkout_session)
        editTextCountryCode = findViewById(R.id.editText_country_code)
        editTextPaymentMethod = findViewById(R.id.editText_payment_method)
        buttonStartPayment = findViewById(R.id.button_start_payment)
        buttonSubmitPayment = findViewById(R.id.button_submit_payment)
        buttonContinuePayment = findViewById(R.id.button_continue_payment)
        textViewOtt = findViewById(R.id.textView_ott)
        progressBar = findViewById(R.id.progress_bar)
        fragmentContainer = findViewById(R.id.payment_fragment_container)
        configurationContainer = findViewById(R.id.linearLayout_configuration)

        // Pre-fill with test data
        editTextCheckoutSession.setText(BuildConfig.YUNO_TEST_CHECKOUT_SESSION)
        editTextCountryCode.setText(BuildConfig.YUNO_TEST_COUNTRY_CODE)
        editTextPaymentMethod.setText("CARD")

        // Hide submit button initially (shown when form is loaded)
        buttonSubmitPayment.visibility = GONE
        buttonContinuePayment.visibility = GONE
        textViewOtt.visibility = GONE
    }

    private fun initListeners() {
        // Button to start the payment render flow
        buttonStartPayment.setOnClickListener {
            if (editTextCheckoutSession.isValid &&
                editTextCountryCode.isValid &&
                editTextPaymentMethod.isValid
            ) {
                startPaymentRenderFlow()
            }
        }

        // Merchant's custom submit button
        buttonSubmitPayment.setOnClickListener {
            if (::fragmentController.isInitialized) {
                fragmentController.submitForm()
            }
        }

        // Continue payment button (after creating payment in backend)
        buttonContinuePayment.setOnClickListener {
            if (::fragmentController.isInitialized) {
                // Hide the OTT and continue button
                textViewOtt.visibility = GONE
                buttonContinuePayment.visibility = GONE

                // Continue with the payment flow (3DS, redirects, etc.)
                fragmentController.continuePayment()
            }
        }

        // Click on OTT TextView to copy to clipboard
        textViewOtt.setOnClickListener {
            val ottText = textViewOtt.text.toString()
            // Extract just the token part (remove "One-Time Token: " prefix)
            val token = ottText.substringAfter("One-Time Token: ")
            
            // Copy to clipboard
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("One-Time Token", token)
            clipboard.setPrimaryClip(clip)
            
            // Show feedback
            Toast.makeText(this, "OTT copied to clipboard!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startPaymentRenderFlow() {


        // IMPORTANT: Update the checkout session and country before starting the payment render
        // This configures the SDK with the session and country that will be used for the payment
        updateCheckoutSession(
            checkoutSession = editTextCheckoutSession.text.toString(),
            countryCode = editTextCountryCode.text.toString().uppercase()
        )

        // Start the payment render mode
        // This function displays the payment form fragment in the UI
        // Note: startCheckout() must have been called in onCreate() before this
        fragmentController = startPaymentRender(
            checkoutSession = editTextCheckoutSession.text.toString(),
            countryCode = editTextCountryCode.text.toString().uppercase(),
            coroutineScope = lifecycleScope,
            paymentSelected = PaymentSelected(
                vaultedToken = null,
                paymentMethodType = editTextPaymentMethod.text.toString().uppercase()
            ),
            listener = this
        )
    }

    // YunoPaymentRenderListener implementation

    /**
     * Receives the fragment to display in the UI container.
     * @param fragment The fragment containing the payment form
     */
    override fun showView(fragment: Fragment) {
        // Replace the fragment in the container
        supportFragmentManager.beginTransaction()
            .replace(R.id.payment_fragment_container, fragment)
            .commit()

        // Hide configuration container (inputs and start button)
        configurationContainer.visibility = GONE

        // Show merchant's submit button below the form
        buttonSubmitPayment.visibility = VISIBLE
        buttonSubmitPayment.isEnabled = true

        Log.d(
            "PaymentRender",
            "Payment fragment loaded, configuration hidden, merchant submit button visible"
        )
    }

    /**
     * Receives the one-time token when the customer completes the form.
     * @param oneTimeToken The token needed to create the payment
     * @param additionalData Additional information about the token
     */
    override fun returnOneTimeToken(oneTimeToken: String, additionalData: OneTimeTokenModel?) {
        Log.d("PaymentRender", "One-time token received: $oneTimeToken")

        // Remove the SDK form fragment
        supportFragmentManager.findFragmentById(R.id.payment_fragment_container)?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }

        // Display the OTT in the TextView
        textViewOtt.text = "One-Time Token: $oneTimeToken (tap to copy)"
        textViewOtt.visibility = VISIBLE

        // Show the continue payment button
        buttonContinuePayment.visibility = VISIBLE
        buttonContinuePayment.isEnabled = true

        // Hide the submit button as it's no longer needed
        buttonSubmitPayment.visibility = GONE

        // Here the merchant would call their backend to create the payment with this OTT
        // For this example, we're showing the OTT and letting the user manually continue
        Toast.makeText(
            this,
            "OTT received! SDK form removed. Create payment in backend, then click Continue",
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Receives the final payment status.
     * @param resultCode Result code from the payment process
     * @param paymentStatus The final payment status (SUCCEEDED, FAIL, etc.)
     * @param paymentSubStatus Additional detail about the payment status
     */
    override fun returnStatus(resultCode: Int, paymentStatus: String, paymentSubStatus: String?) {
        Log.d(
            "PaymentRender",
            "Payment Status: $paymentStatus, Sub Status: $paymentSubStatus, Result Code: $resultCode"
        )

        // Handle the payment result
        when (paymentStatus) {
            "SUCCEEDED" -> {
                Log.i("PaymentRender", "Payment succeeded!")

                // Remove the fragment
                clearPaymentFragment()

                // Show success toast
                Toast.makeText(this, "Payment completed successfully!", Toast.LENGTH_LONG).show()

                // Close activity after a delay
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 2000)
            }

            "FAIL" -> {
                Log.e("PaymentRender", "Payment failed")

                // Remove the fragment
                clearPaymentFragment()

                // Show error toast
                Toast.makeText(this, "Payment failed. Please try again.", Toast.LENGTH_LONG).show()

                // Re-enable start button for retry
                buttonStartPayment.isEnabled = true
            }

            "CANCELED" -> {
                Log.w("PaymentRender", "Payment canceled by user")

                // Remove the fragment
                clearPaymentFragment()

                // Show canceled toast
                Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show()

                // Close activity
                finish()
            }

            "PROCESSING" -> {
                Log.i("PaymentRender", "Payment is being processed")

                // Show processing toast
                val message = if (paymentSubStatus != null) {
                    "Payment is being processed - $paymentSubStatus"
                } else {
                    "Payment is being processed"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }

            "REJECT" -> {
                Log.w("PaymentRender", "Payment rejected")

                // Remove the fragment
                clearPaymentFragment()

                // Show rejected toast
                Toast.makeText(this, "Payment rejected", Toast.LENGTH_LONG).show()

                // Re-enable start button for retry
                buttonStartPayment.isEnabled = true
            }

            else -> {
                Log.w("PaymentRender", "Payment status: $paymentStatus")

                // Remove the fragment for any other status
                clearPaymentFragment()

                // Re-enable start button
                buttonStartPayment.isEnabled = true
            }
        }
    }

    /**
     * Receives loading state changes.
     * @param isLoading True if the SDK is processing, false otherwise
     */
    override fun loadingListener(isLoading: Boolean) {
        // Show/hide progress bar
        progressBar.isVisible = isLoading

        Log.d("PaymentRender", "Loading state: $isLoading")
    }

    /**
     * Clears the payment fragment from the container and shows configuration fields again
     */
    private fun clearPaymentFragment() {
        supportFragmentManager.findFragmentById(R.id.payment_fragment_container)?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }

        // Show configuration fields again for retry
        configurationContainer.visibility = VISIBLE

        // Hide merchant submit button
        buttonSubmitPayment.visibility = GONE

        // Hide OTT and continue button
        textViewOtt.visibility = GONE
        buttonContinuePayment.visibility = GONE
    }
}
