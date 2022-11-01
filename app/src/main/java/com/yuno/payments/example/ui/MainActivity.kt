package com.yuno.payments.example.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yuno.payments.base.extensions.style
import com.yuno.payments.example.ui.payment.CheckoutLiteActivity
import com.yuno.payments.core.Yuno
import com.yuno.payments.example.R
import com.yuno.payments.example.ui.enrollment.EnrollPaymentActivity
import com.yuno.payments.example.ui.payment.CheckoutCompleteActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListeners()
        findViewById<TextView>(R.id.textView_secure_payment).text =
            SpannableStringBuilder().style(this, R.style.TextSmall_LowTextGrey) {
                append(getString(R.string.secure_payment))
            }.style(this, R.style.TextSmall_PurpleLight_Bold) {
                append(" YUNO")
            }
    }

    private fun initListeners() {
        findViewById<Button>(R.id.button_add_payment).setOnClickListener {
            startActivity(Intent(this, EnrollPaymentActivity::class.java))
        }

        findViewById<Button>(R.id.button_payment_lite)
            .setOnClickListener {
                startActivity(Intent(this, CheckoutLiteActivity::class.java))
            }

        findViewById<Button>(R.id.button_payment_complete)
            .setOnClickListener {
                startActivity(Intent(this, CheckoutCompleteActivity::class.java))
            }
    }
}