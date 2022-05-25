package com.yuno.payments.example.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.yuno.payments.example.R
import com.yuno.payments.features.payment.startPaymentWithYuno
import com.yuno.payments.features.payment.ui.views.PaymentMethodListView

class PaymentFlowActivity : AppCompatActivity() {

    private lateinit var sessionId: String

    private lateinit var paymentMethodListView: PaymentMethodListView

    private lateinit var editTextSessionId: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_flow)
        initViews()
        initListeners()
    }

    private fun initViews() {
        editTextSessionId = findViewById(R.id.editText_session_id)
        paymentMethodListView = findViewById(R.id.list_payment_methods)
    }

    private fun initListeners() {
        findViewById<Button>(R.id.button_set_session_id)
            .setOnClickListener {
                sessionId = editTextSessionId.text.toString()
                paymentMethodListView.setCheckoutSession(sessionId)
            }

        findViewById<Button>(R.id.button_pay)
            .setOnClickListener {
                paymentMethodListView.getPaymentSelected()?.let { paymentSelected ->
                    startPaymentWithYuno(
                        checkoutSession = sessionId,
                        countryCode = "BR",
                        paymentSelected = paymentSelected,
                        createPaymentFunction = this::createPayment,
                    )
                }
            }
    }

    private suspend fun createPayment(token: String): String {
        //Call your back to create the payment
        return token
    }
}