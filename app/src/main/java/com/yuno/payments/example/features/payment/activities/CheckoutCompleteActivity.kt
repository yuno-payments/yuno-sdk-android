package com.yuno.payments.example.features.payment.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.yuno.payments.example.features.payment.ui.CheckoutCompleteScreen
import com.yuno.payments.example.features.payment.viewmodel.CheckoutCompleteViewModel
import com.yuno.payments.example.ui.theme.YunoTheme
import com.yuno.sdk.payments.continuePayment
import com.yuno.sdk.payments.startCheckout
import com.yuno.sdk.payments.startPayment
import com.yuno.sdk.payments.updateCheckoutSession

/**
 * Checkout Complete — demonstrates the full payment flow where the SDK provides
 * its own payment method list (PaymentMethodListViewComponent) and handles
 * the payment form UI via startPayment().
 *
 * Extends AppCompatActivity (not ComponentActivity) for consistency with the other
 * SDK-integrated activities and to avoid potential runtime crashes if PaymentMethodListViewComponent
 * internally uses supportFragmentManager or casts the Activity to AppCompatActivity.
 *
 * Flow: startCheckout() -> updateCheckoutSession() -> user selects method -> startPayment()
 *       -> OTT received -> create payment on backend -> continuePayment()
 *
 * SDK constraint: startCheckout() MUST be called in onCreate() before any other SDK calls.
 * Note: checkout session and country are NOT passed to startCheckout() here because the user
 * enters them in the UI. The session is provided later via updateCheckoutSession().
 * (Contrast with CheckoutLiteActivity, which pre-fills from BuildConfig for quick demo purposes.)
 */
class CheckoutCompleteActivity : AppCompatActivity() {

    private val viewModel: CheckoutCompleteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // CRITICAL: startCheckout() must be called in onCreate() before setContent or any UI.
        // It registers lifecycle observers and initializes SDK internals. Calling it later
        // (e.g., on a button click) causes "Object not injected" crashes.
        // No checkout session is passed here — the user enters it in the UI, then it is
        // applied via updateCheckoutSession(). startCheckout() only needs a session to pre-load
        // payment methods; since this flow defers that to user input, passing no session is valid.
        startCheckout(
            callbackPaymentState = { paymentState, paymentSubState ->
                paymentState?.let {
                    viewModel.onPaymentStateChange(it)
                    Log.d("CheckoutComplete", "State: $it, Sub-State: $paymentSubState")
                }
            },
        )

        setContent {
            YunoTheme {
                CheckoutCompleteScreen(
                    viewModel = viewModel,
                    onUpdateCheckoutSession = { session, country ->
                        updateCheckoutSession(session, country)
                    },
                    onStartPayment = {
                        startPayment(
                            callbackOTT = { token ->
                                token?.let {
                                    viewModel.onTokenUpdated(it)
                                    Log.d("CheckoutComplete", "OTT received: $it")
                                }
                            },
                        )
                    },
                    // continuePayment() tells the SDK to finalize payment after the merchant
                    // creates it on the backend using the OTT.
                    onContinuePayment = {
                        continuePayment(
                            callbackPaymentState = { paymentState, paymentSubState ->
                                paymentState?.let {
                                    viewModel.onPaymentStateChange(it)
                                    Log.d("CheckoutComplete", "State: $it, Sub-State: $paymentSubState")
                                }
                            },
                        )
                    },
                )
            }
        }
    }
}
