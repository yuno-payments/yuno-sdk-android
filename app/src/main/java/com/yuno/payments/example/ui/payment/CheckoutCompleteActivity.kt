package com.yuno.payments.example.ui.payment

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
import com.yuno.payments.features.payment.continuePayment
import com.yuno.payments.features.payment.startCheckout
import com.yuno.payments.features.payment.startPayment
import com.yuno.payments.features.payment.ui.views.PaymentMethodListView
import com.yuno.payments.features.payment.updateCheckoutSession

class CheckoutCompleteActivity : AppCompatActivity() {

    private lateinit var checkoutSessionEditText: CustomEditText
    private lateinit var countryCodeEditText: CustomEditText
    private lateinit var paymentMethodListContainer: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_complete)
        startCheckout(
            checkoutSession = "",
            countryCode = "",
            callbackOTT = ::onTokenUpdated,
            callbackPaymentState = ::onPaymentStateChange,
        )
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

    private fun updateYunoList() {
        updateCheckoutSession(
            checkoutSession = checkoutSessionEditText.text.toString(),
            countryCode = countryCodeEditText.text.toString(),
        )
        val paymentMethodListView = PaymentMethodListView(this)
        paymentMethodListContainer.removeAllViews()
        paymentMethodListContainer.addView(
            paymentMethodListView
        )
        paymentMethodListContainer.visibility = View.VISIBLE
    }

    private fun onTokenUpdated(token: String?) {
        token?.let {
            updateView(token)
            Log.e("Payment flow", "success ---> token: $token")
        }
    }

    private fun onPaymentStateChange(paymentState: String?) {
        paymentState?.let {
            restartView()
            Log.e("Payment State", it)
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
