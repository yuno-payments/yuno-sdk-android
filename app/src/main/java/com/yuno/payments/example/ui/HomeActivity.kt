package com.yuno.payments.example.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yuno.payments.example.features.enrollment.activities.EnrollmentLiteActivity
import com.yuno.payments.example.features.enrollment.activities.EnrollmentRenderActivity
import com.yuno.payments.example.features.payment.activities.CheckoutCompleteActivity
import com.yuno.payments.example.features.payment.activities.CheckoutLiteActivity
import com.yuno.payments.example.features.payment.activities.PaymentRenderActivity
import com.yuno.payments.example.ui.theme.YunoTheme

/**
 * Entry point for the Yuno SDK demo app.
 *
 * HomeActivity uses ComponentActivity (no fragment support needed) and simply navigates
 * to the different SDK integration demos. No SDK initialization is required here —
 * the SDK is initialized per-flow in each target Activity's onCreate().
 */
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YunoTheme {
                HomeScreen(
                    onEnrollmentLiteClick = { startActivity(Intent(this, EnrollmentLiteActivity::class.java)) },
                    onEnrollmentRenderClick = { startActivity(Intent(this, EnrollmentRenderActivity::class.java)) },
                    onCheckoutLiteClick = { startActivity(Intent(this, CheckoutLiteActivity::class.java)) },
                    onPaymentRenderClick = { startActivity(Intent(this, PaymentRenderActivity::class.java)) },
                    onCheckoutCompleteClick = { startActivity(Intent(this, CheckoutCompleteActivity::class.java)) },
                )
            }
        }
    }
}
