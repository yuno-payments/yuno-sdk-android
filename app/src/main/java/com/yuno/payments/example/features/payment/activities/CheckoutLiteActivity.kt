package com.yuno.payments.example.features.payment.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.yuno.payments.example.BuildConfig
import com.yuno.payments.example.features.payment.ui.CheckoutLiteScreen
import com.yuno.payments.example.features.payment.viewmodel.CheckoutLiteViewModel
import com.yuno.payments.example.ui.theme.YunoTheme
import com.yuno.presentation.core.components.PaymentSelected
import com.yuno.sdk.payments.continuePayment
import com.yuno.sdk.payments.startCheckout
import com.yuno.sdk.payments.startPaymentLite
import com.yuno.sdk.payments.updateCheckoutSession

/**
 * Checkout Lite — demonstrates the "lite" payment flow where the merchant controls
 * the payment method selection UI and calls startPaymentLite() with the chosen method.
 *
 * SDK constraint: startCheckout() MUST be called in onCreate() before any other SDK calls.
 * It initializes internal SDK state; calling it later (e.g., on button press) will crash.
 */
class CheckoutLiteActivity : ComponentActivity() {

    private val viewModel: CheckoutLiteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // CRITICAL: startCheckout() must be called in onCreate() before setContent or any UI.
        // It registers lifecycle observers and initializes SDK internals. Calling it later
        // (e.g., on a button click) causes "Object not injected" crashes.
        startCheckout(
            checkoutSession = BuildConfig.YUNO_TEST_CHECKOUT_SESSION,
            countryCode = BuildConfig.YUNO_TEST_COUNTRY_CODE.uppercase(),
            // callbackPaymentState fires after continuePayment() resolves the final status.
            // This is a payment status string ("SUCCEEDED", "FAIL", etc.) — NOT a token.
            // Reset the flow so the user can start a new payment.
            callbackPaymentState = { paymentState, paymentSubState ->
                paymentState?.let {
                    Log.d("CheckoutLite", "Payment State: $it, Sub-State: $paymentSubState")
                    viewModel.onPaymentStateReceived()
                }
            },
        )

        setContent {
            YunoTheme {
                CheckoutLiteScreen(
                    viewModel = viewModel,
                    onUpdateCheckoutSession = { session, country ->
                        updateCheckoutSession(session, country)
                    },
                    onStartPaymentLite = { paymentMethodType, vaultedToken ->
                        startPaymentLite(
                            paymentSelected = PaymentSelected(
                                paymentMethodType = paymentMethodType,
                                vaultedToken = vaultedToken,
                            ),
                            // callBackTokenWithInformation provides additional OTT metadata
                            // (card brand, last four digits, etc.) alongside the token.
                            callBackTokenWithInformation = { ottModel ->
                                Log.i("CheckoutLite", "OTT with info: $ottModel")
                            },
                            // callbackOTT provides just the one-time token string.
                            // Use this token to create a payment on your backend.
                            callbackOTT = { token ->
                                token?.let {
                                    Log.d("CheckoutLite", "OTT received: $it")
                                    viewModel.onOttReceived(it)
                                }
                            },
                        )
                    },
                    onContinuePayment = { continuePayment() },
                )
            }
        }
    }
}
