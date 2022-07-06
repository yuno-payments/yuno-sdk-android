package com.yuno.payments.example.ui.payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yuno.payments.example.BuildConfig
import com.yuno.payments.example.R
import com.yuno.payments.features.payment.*
import com.yuno.payments.features.payment.ui.views.PaymentSelected

class CheckoutLiteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_lite)
        startCheckout("", countryCode = "CO")
        initListeners()
    }

    private fun initListeners() {
        findViewById<Button>(R.id.button_pay)
            .setOnClickListener {
                startPaymentLite(
                    paymentSelected = PaymentSelected(
                        paymentMethodType = findViewById<EditText>(R.id.editText_type).text.toString(),
                        vaultedToken = findViewById<EditText>(R.id.editText_token).text.toString(),
                    ),
                )
            }

        findViewById<Button>(R.id.button_continue)
            .setOnClickListener {
                continuePayment()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == YUNO_START_PAYMENT_REQUEST_CODE) {
            if (resultCode == PAYMENT_RESULT_SUCCESS) {
                val token = data?.getStringExtra(PAYMENT_RESULT_DATA_TOKEN)
                updateView(token)
                Log.e("payment", "success ---> token: $token")
            }
            if (resultCode == PAYMENT_RESULT_ERROR) {
                Log.e("payment", "fail")
            }
            if (resultCode == PAYMENT_RESULT_CANCELED) {
                Log.e("payment", "cancelled")
            }
        }
    }

    private fun updateView(token: String?) {
        findViewById<TextView>(R.id.textView_token).text = "Token: ${token.orEmpty()}"
        findViewById<LinearLayout>(R.id.layout_checkout_continue).visibility = View.VISIBLE
    }
}
