package com.yuno.payments.example.features.payment.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.constraintlayout.widget.ConstraintLayout
import com.yuno.payments.example.BuildConfig
import com.yuno.payments.example.R
import com.yuno.payments.example.ui.views.CustomEditText
import com.yuno.presentation.core.components.PaymentMethodListViewComponent
import com.yuno.sdk.payments.continuePayment
import com.yuno.sdk.payments.startCheckout
import com.yuno.sdk.payments.startPayment
import com.yuno.sdk.payments.updateCheckoutSession

class CheckoutCompleteActivity : AppCompatActivity() {

    private lateinit var checkoutSessionEditText: CustomEditText
    private lateinit var countryCodeEditText: CustomEditText
    private lateinit var paymentMethodListContainer: ScrollView
    private lateinit var textViewToken: TextView
    private lateinit var clipboardManager: ClipboardManager
    private var dynamicPaymentMethods = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_complete)

        startCheckout(
            callbackPaymentState = this::onPaymentStateChange,
        )
        initViews()
        initListeners()
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (intent.hasExtra("dynamicPaymentMethods")) {
            dynamicPaymentMethods = intent.getBooleanExtra("dynamicPaymentMethods", false)
        }
    }

    private fun initViews() {
        checkoutSessionEditText = findViewById(R.id.editText_checkoutSession)
        countryCodeEditText = findViewById(R.id.editText_countryCode)
        textViewToken = findViewById(R.id.textView_token)
        checkoutSessionEditText.setText(BuildConfig.YUNO_TEST_CHECKOUT_SESSION)
        countryCodeEditText.setText(BuildConfig.YUNO_TEST_COUNTRY_CODE)
    }

    private fun initListeners() {
        findViewById<Button>(R.id.button_pay)
            .setOnClickListener {
                startPayment(callbackOTT = this::onTokenUpdated)
            }

        findViewById<Button>(R.id.button_continue)
            .setOnClickListener {
                continuePayment(callbackPaymentState = this::onPaymentStateChange)
            }

        findViewById<Button>(R.id.button_setCheckoutSession)
            .setOnClickListener { updateYunoList() }

        findViewById<Button>(R.id.button_copy)
            .setOnClickListener { copyToken() }
    }

    private fun copyToken() {
        val tokenCopied: ClipData = ClipData.newPlainText("Token", textViewToken.text.toString())
        clipboardManager.setPrimaryClip(tokenCopied)
        Toast.makeText(this, "token copied", Toast.LENGTH_SHORT).show()
    }

    private fun updateYunoList() {
        updateCheckoutSession(
            checkoutSessionEditText.text.toString(),
            countryCodeEditText.text.toString(),
        )

        val composeView = findViewById<ComposeView>(R.id.compose_paymentListContainer)

        composeView.setContent {

            PaymentMethodListViewComponent(
                this,
                onPaymentSelected = {
                    findViewById<Button>(R.id.button_pay).isEnabled = it
                },

                )
        }
    }


    private fun onTokenUpdated(token: String?) {
        token?.let {
            updateView(token)
            Log.e("Payment flow", "success ---> token: $token")
        }
    }

    /**
     * Callback that receives the payment state changes from Yuno SDK.
     * @param paymentState The main payment state (SUCCEEDED, FAIL, PROCESSING, REJECT, INTERNAL_ERROR, CANCELED)
     * @param paymentSubState Additional payment sub-state information providing more details about the payment status
     */
    private fun onPaymentStateChange(paymentState: String?, paymentSubState: String?) {
        paymentState?.let {
            restartView()
            Log.e("Payment State", "State: $it, Payment Sub-State: $paymentSubState")
        }
    }

    private fun updateView(token: String?) {
        paymentMethodListContainer.visibility = View.GONE
        checkoutSessionEditText.visibility = View.GONE
        countryCodeEditText.visibility = View.GONE
        findViewById<Button>(R.id.button_setCheckoutSession).visibility = View.GONE
        findViewById<Button>(R.id.button_pay).isEnabled = false
        textViewToken.text = token.orEmpty()
        findViewById<ConstraintLayout>(R.id.layout_checkout_continue).visibility = View.VISIBLE
    }

    private fun restartView() {
        paymentMethodListContainer.visibility = View.VISIBLE
        checkoutSessionEditText.visibility = View.VISIBLE
        countryCodeEditText.visibility = View.VISIBLE
        findViewById<Button>(R.id.button_setCheckoutSession).visibility = View.VISIBLE
        textViewToken.text = ""
        findViewById<ConstraintLayout>(R.id.layout_checkout_continue).visibility = View.GONE
    }
}
