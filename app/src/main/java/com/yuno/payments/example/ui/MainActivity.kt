package com.yuno.payments.example.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.yuno.payments.example.ui.payment.CheckoutLiteActivity
import com.yuno.payments.core.Yuno
import com.yuno.payments.example.R
import com.yuno.payments.example.ui.enrollment.EnrollPaymentActivity
import com.yuno.payments.example.ui.payment.CheckoutCompleteActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setListeners()
    }

    private fun setListeners() {
        findViewById<Button>(R.id.button_change_api_key).setOnClickListener {
            Yuno.initialize(
                this.application,
                findViewById<EditText>(R.id.editText_api_key).text.toString()
            )
        }

        findViewById<Button>(R.id.button_start_enrollment_flow).setOnClickListener {
            startActivity(
                Intent(this, EnrollPaymentActivity::class.java)
            )
        }

        findViewById<Button>(R.id.button_payment_lite).setOnClickListener {
            startActivity(
                Intent(this, CheckoutLiteActivity::class.java)
            )
        }

        findViewById<Button>(R.id.button_payment_complete).setOnClickListener {
            startActivity(
                Intent(this, CheckoutCompleteActivity::class.java)
            )
        }
    }
}