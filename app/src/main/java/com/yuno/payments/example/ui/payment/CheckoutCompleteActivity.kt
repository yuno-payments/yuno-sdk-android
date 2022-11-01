package com.yuno.payments.example.ui.payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yuno.payments.example.R
import com.yuno.payments.example.ui.views.CustomEditText
import com.yuno.payments.features.payment.PAYMENT_RESULT_CANCELED
import com.yuno.payments.features.payment.PAYMENT_RESULT_DATA_STATE
import com.yuno.payments.features.payment.PAYMENT_RESULT_DATA_TOKEN
import com.yuno.payments.features.payment.PAYMENT_RESULT_ERROR
import com.yuno.payments.features.payment.PAYMENT_RESULT_SUCCESS
import com.yuno.payments.features.payment.YUNO_CONTINUE_PAYMENT_REQUEST_CODE
import com.yuno.payments.features.payment.YUNO_START_PAYMENT_REQUEST_CODE
import com.yuno.payments.features.payment.continuePayment
import com.yuno.payments.features.payment.startCheckout
import com.yuno.payments.features.payment.startPayment
import com.yuno.payments.features.payment.ui.views.PaymentMethodListView

class CheckoutCompleteActivity : AppCompatActivity() {

    private lateinit var checkoutSessionEditText : CustomEditText
    private lateinit var countryCodeEditText : CustomEditText
    private lateinit var paymentMethodListContainer : ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_complete)
        startCheckout(checkoutSession = "", countryCode = "")
        initViews()
        initListeners()
    }

    private fun initViews() {
        checkoutSessionEditText = findViewById(R.id.editText_checkoutSession)
        countryCodeEditText = findViewById(R.id.editText_countryCode)
        paymentMethodListContainer = findViewById(R.id.scrollView_paymentListContainer)
    }

    private fun initListeners() {
        findViewById<Button>(R.id.button_pay)
            .setOnClickListener { startPayment() }

        findViewById<Button>(R.id.button_continue)
            .setOnClickListener { continuePayment() }

        findViewById<Button>(R.id.button_setCheckoutSession)
            .setOnClickListener { updateYunoList() }
    }

    private fun updateYunoList(){
        startCheckout(checkoutSessionEditText.text.toString(),countryCodeEditText.text.toString())
        val paymentMethodListView = PaymentMethodListView(this)
        paymentMethodListContainer.removeAllViews()
        paymentMethodListContainer.addView(
            paymentMethodListView
        )
        paymentMethodListContainer.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == YUNO_START_PAYMENT_REQUEST_CODE) {
            if (resultCode == PAYMENT_RESULT_SUCCESS) {
                val token = data?.getStringExtra(PAYMENT_RESULT_DATA_TOKEN)
                updateView(token)
                Log.e("Payment flow", "success ---> token: $token")
            }
            if (resultCode == PAYMENT_RESULT_ERROR) {
                Log.e("Payment Flow", "fail")
            }
            if (resultCode == PAYMENT_RESULT_CANCELED) {
                Log.e("Payment flow", "cancelled")
            }
        }
        if (requestCode == YUNO_CONTINUE_PAYMENT_REQUEST_CODE) {
            val paymentState: String? = data?.getStringExtra(PAYMENT_RESULT_DATA_STATE)
            paymentState?.let { Log.e("Payment State", it) }

            if (resultCode == PAYMENT_RESULT_SUCCESS) {
                restartView()
            }
        }
        if (requestCode == YUNO_CONTINUE_PAYMENT_REQUEST_CODE) {
            if (resultCode == PAYMENT_RESULT_SUCCESS) {
                restartView()
            }
        }
    }

    private fun updateView(token: String?) {
        paymentMethodListContainer.visibility = View.GONE
        checkoutSessionEditText.visibility = View.GONE
        findViewById<Button>(R.id.button_setCheckoutSession).visibility = View.GONE
        findViewById<Button>(R.id.button_pay).isEnabled = false
        findViewById<TextView>(R.id.textView_token).text = "Token: ${token.orEmpty()}"
        findViewById<LinearLayout>(R.id.layout_checkout_continue).visibility = View.VISIBLE
    }

    private fun restartView() {
        paymentMethodListContainer.visibility = View.VISIBLE
        checkoutSessionEditText.visibility = View.VISIBLE
        findViewById<Button>(R.id.button_setCheckoutSession).visibility = View.VISIBLE
        findViewById<TextView>(R.id.textView_token).text = "Token:"
        findViewById<LinearLayout>(R.id.layout_checkout_continue).visibility = View.GONE
    }
}
