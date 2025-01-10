package com.yuno.payments.example.features.payment.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.yuno.payments.example.BuildConfig
import com.yuno.payments.example.R
import com.yuno.payments.example.ui.views.CustomEditText
import com.yuno.payments.features.payment.continuePayment
import com.yuno.payments.features.payment.startCheckout
import com.yuno.payments.features.payment.startPaymentLite
import com.yuno.payments.features.payment.ui.views.PaymentSelected
import com.yuno.payments.features.payment.updateCheckoutSession

class CheckoutLiteActivity : AppCompatActivity() {

    private lateinit var clipboardManager: ClipboardManager

    private lateinit var editTextCheckoutSession: CustomEditText

    private lateinit var editTextCountryCode: CustomEditText

    private lateinit var editTextVaultedToken: CustomEditText

    private lateinit var paymentMethodType: CustomEditText

    private lateinit var constraintCheckout: ConstraintLayout

    private lateinit var constraintPayment: ConstraintLayout

    private lateinit var constraintLayoutContinue: ConstraintLayout

    private lateinit var buttonSetCheckout: Button

    private lateinit var buttonPay: Button

    private lateinit var buttonContinue: Button

    private lateinit var buttonCopy: Button

    private lateinit var buttonReset: Button

    private lateinit var textViewToken: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_lite)
        initViews()
        startCheckout(
            checkoutSession = editTextCheckoutSession.text.toString(),
            countryCode = editTextCountryCode.text.toString().uppercase(),
            callbackPaymentState = this::onPaymentStateChange,
        )
    }

    private fun initViews() {
        editTextCheckoutSession = findViewById(R.id.editText_checkoutSession)
        editTextCountryCode = findViewById(R.id.editText_countryCode)
        editTextCheckoutSession.setText(BuildConfig.YUNO_TEST_CHECKOUT_SESSION)
        editTextCountryCode.setText(BuildConfig.YUNO_TEST_COUNTRY_CODE)
        editTextVaultedToken = findViewById(R.id.editText_paymentToken)
        paymentMethodType = findViewById(R.id.editText_methodType)

        constraintCheckout = findViewById(R.id.constraintLayout_checkoutModel)
        constraintPayment = findViewById(R.id.constraintLayout_paymentModel)
        constraintLayoutContinue = findViewById(R.id.layout_checkoutContinue)

        buttonSetCheckout = findViewById(R.id.button_setCheckout_session)
        buttonPay = findViewById(R.id.button_pay)
        buttonContinue = findViewById(R.id.button_continue)
        buttonReset = findViewById(R.id.button_reset)
        buttonCopy = findViewById(R.id.button_copy)

        textViewToken = findViewById(R.id.textView_token)
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        initListeners()
    }

    private fun initListeners() {
        buttonSetCheckout.setOnClickListener { setCheckoutSession() }
        buttonPay.setOnClickListener { startPaymentLite() }
        buttonContinue.setOnClickListener { continuePayment() }
        buttonCopy.setOnClickListener { copyToken() }
        buttonReset.setOnClickListener { restartActivity() }
    }

    private fun setCheckoutSession() {
        if (editTextCheckoutSession.isValid && editTextCountryCode.isValid) {
            updateCheckoutSession(
                checkoutSession = editTextCheckoutSession.text.toString(),
                countryCode = editTextCountryCode.text.toString().uppercase(),
            )
            updateEditTextPayments()
        }
    }

    private fun updateEditTextPayments() {
        constraintCheckout.visibility = View.GONE
        constraintPayment.visibility = View.VISIBLE
        buttonPay.isEnabled = true
    }

    private fun startPaymentLite() {
        if (paymentMethodType.isValid) {
            startPaymentLite(
                paymentSelected = PaymentSelected(
                    paymentMethodType = paymentMethodType.text.toString(),
                    vaultedToken = editTextVaultedToken.text.toString(),
                ),
                callBackTokenWithInformation = { ottModel ->
                    Log.i("ott information", ottModel.toString())
                },
                callbackOTT = this::onTokenUpdated,
            )
        }
    }

    private fun onTokenUpdated(token: String?) {
        token?.let {
            updateView(token)
            Log.e("Payment flow", "success ---> token: $token")
        }
    }

    private fun onPaymentStateChange(paymentState: String?) {
        paymentState?.let {
            updateView(paymentState)
        }
    }

    private fun updateView(token: String?) {
        buttonPay.isEnabled = false
        editTextVaultedToken.isEnabled = false
        paymentMethodType.isEnabled = false
        textViewToken.text = token.orEmpty()
        constraintLayoutContinue.visibility = View.VISIBLE
    }

    private fun restartActivity() {
        constraintCheckout.visibility = View.VISIBLE
        constraintPayment.visibility = View.GONE
        editTextVaultedToken.isEnabled = true
        paymentMethodType.isEnabled = true
        constraintLayoutContinue.visibility = View.GONE
        textViewToken.text = ""
        paymentMethodType.setText("")
        editTextVaultedToken.setText("")
    }

    private fun copyToken() {
        val tokenCopied: ClipData = ClipData.newPlainText("Token", textViewToken.text.toString())
        clipboardManager.setPrimaryClip(tokenCopied)
        Toast.makeText(this, "token copied", Toast.LENGTH_SHORT).show()
    }
}
