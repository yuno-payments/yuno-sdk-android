package com.yuno.payments.example.features.payment.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yuno.payments.example.R
import com.yuno.payments.example.features.payment.ui.PaymentRenderScreen
import com.yuno.payments.example.features.payment.viewmodel.PaymentRenderViewModel
import com.yuno.payments.features.payment.models.OneTimeTokenModel
import com.yuno.presentation.core.components.PaymentSelected
import com.yuno.sdk.payments.render.YunoPaymentFragmentController
import com.yuno.sdk.payments.render.YunoPaymentRenderListener
import com.yuno.sdk.payments.render.startPaymentRender
import com.yuno.sdk.payments.startCheckout
import com.yuno.sdk.payments.updateCheckoutSession

/**
 * Payment Render — demonstrates rendering the SDK payment form directly inside
 * a merchant-controlled layout using startPaymentRender().
 *
 * IMPORTANT: This Activity must extend AppCompatActivity (not ComponentActivity) because
 * the SDK renders its payment form as a Fragment. The Fragment is committed into a
 * FrameLayout via supportFragmentManager, which requires AppCompatActivity.
 *
 * Flow: startCheckout() -> updateCheckoutSession() -> startPaymentRender()
 *       -> SDK calls showView() with Fragment -> merchant calls submitForm()
 *       -> returnOneTimeToken() -> create payment on backend -> continuePayment()
 */
class PaymentRenderActivity : AppCompatActivity(), YunoPaymentRenderListener {

    private lateinit var fragmentController: YunoPaymentFragmentController
    private val viewModel: PaymentRenderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // CRITICAL: startCheckout() must be called in onCreate() before startPaymentRender().
        // It registers lifecycle observers and initializes SDK internals. Calling it later
        // (e.g., on a button click) causes "Object not injected" crashes.
        // No checkout session is passed here — it will be set later via updateCheckoutSession().
        startCheckout()

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            PaymentRenderScreen(
                uiState = uiState,
                checkoutSession = viewModel.checkoutSession,
                countryCode = viewModel.countryCode,
                paymentMethod = viewModel.paymentMethod,
                showErrors = viewModel.showErrors,
                onCheckoutSessionChange = viewModel::onCheckoutSessionChange,
                onCountryCodeChange = viewModel::onCountryCodeChange,
                onPaymentMethodChange = viewModel::onPaymentMethodChange,
                onStartPayment = {
                    if (viewModel.validateAndStart()) {
                        startPaymentRenderFlow()
                    }
                },
                onSubmitPayment = {
                    if (::fragmentController.isInitialized) {
                        fragmentController.submitForm()
                    }
                },
                onContinuePayment = {
                    if (::fragmentController.isInitialized) {
                        fragmentController.continuePayment()
                    }
                },
                onReset = viewModel::onReset,
            )
        }
    }

    private fun startPaymentRenderFlow() {
        updateCheckoutSession(
            checkoutSession = viewModel.checkoutSession,
            countryCode = viewModel.countryCode.uppercase(),
        )
        fragmentController = startPaymentRender(
            checkoutSession = viewModel.checkoutSession,
            countryCode = viewModel.countryCode.uppercase(),
            coroutineScope = lifecycleScope,
            paymentSelected = PaymentSelected(
                vaultedToken = null,
                paymentMethodType = viewModel.paymentMethod.uppercase(),
            ),
            listener = this,
        )
    }

    // --- YunoPaymentRenderListener callbacks ---
    // The SDK calls these methods to communicate payment form lifecycle events.

    override fun showView(fragment: Fragment) {
        // Transition UI state first so the Config overlay is removed and the FrameLayout is
        // visible, then commit the SDK fragment into it.
        // NOTE: The FrameLayout (R.id.payment_fragment_container) must ALWAYS be present in the
        // Compose hierarchy (see PaymentRenderScreen). If it were inside a conditional block,
        // the fragment manager would fail to find the container when the SDK calls showView().
        viewModel.onFragmentVisible()
        supportFragmentManager.beginTransaction()
            .replace(R.id.payment_fragment_container, fragment)
            .commit()
        Log.d("PaymentRender", "Payment fragment loaded")
    }

    override fun returnOneTimeToken(oneTimeToken: String, additionalData: OneTimeTokenModel?) {
        // Called when the SDK has collected payment info and generated a one-time token.
        // Use this token to create a payment on your backend, then call continuePayment().
        Log.d("PaymentRender", "OTT received: $oneTimeToken")
        supportFragmentManager.findFragmentById(R.id.payment_fragment_container)?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
        }
        viewModel.onOttReceived(oneTimeToken)
        Toast.makeText(
            this,
            "OTT received! Create payment in backend, then tap Continue.",
            Toast.LENGTH_LONG,
        ).show()
    }

    override fun returnStatus(resultCode: Int, paymentStatus: String, paymentSubStatus: String?) {
        // Called when the payment flow reaches a terminal state (SUCCEEDED, FAIL, REJECT, etc.).
        Log.d("PaymentRender", "Status: $paymentStatus, Sub: $paymentSubStatus, Code: $resultCode")
        supportFragmentManager.findFragmentById(R.id.payment_fragment_container)?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
        }
        viewModel.onStatusResult(paymentStatus)
        when (paymentStatus) {
            "SUCCEEDED" -> {
                Toast.makeText(this, "Payment completed successfully!", Toast.LENGTH_LONG).show()
                Handler(Looper.getMainLooper()).postDelayed({ finish() }, 2000)
            }
            "CANCELED" -> {
                Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show()
                finish()
            }
            "FAIL" -> Toast.makeText(this, "Payment failed. Please try again.", Toast.LENGTH_LONG).show()
            "REJECT" -> Toast.makeText(this, "Payment rejected", Toast.LENGTH_LONG).show()
            "PROCESSING" -> {
                val msg = if (paymentSubStatus != null) {
                    "Payment is being processed — $paymentSubStatus"
                } else {
                    "Payment is being processed"
                }
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
            else -> Log.w("PaymentRender", "Unhandled payment status: $paymentStatus")
        }
    }

    override fun loadingListener(isLoading: Boolean) {
        Log.d("PaymentRender", "Loading: $isLoading")
        viewModel.onLoading(isLoading)
    }
}
