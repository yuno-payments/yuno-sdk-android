package com.yuno.payments.example.ui.payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yuno.payments.example.R
import com.yuno.payments.features.payment.*
import com.yuno.payments.features.payment.ui.views.PaymentMethodListView

class CheckoutCompleteActivity : AppCompatActivity() {

    private lateinit var paymentMethodListView: PaymentMethodListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_complete)
        startCheckout("", countryCode = "CO")
        initViews()
        initListeners()
    }

    private fun initViews() {
        paymentMethodListView = findViewById(R.id.list_payment_methods)
    }

    private fun initListeners() {
        findViewById<Button>(R.id.button_pay)
            .setOnClickListener { startPayment() }

        findViewById<Button>(R.id.button_continue)
            .setOnClickListener {
                continuePaymentActivity()
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
