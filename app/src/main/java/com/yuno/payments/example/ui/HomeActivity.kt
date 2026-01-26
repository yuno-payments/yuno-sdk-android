package com.yuno.payments.example.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yuno.payments.example.R
import com.yuno.payments.example.features.enrollment.activities.EnrollmentLiteActivity
import com.yuno.payments.example.features.enrollment.activities.EnrollmentRenderActivity
import com.yuno.payments.example.features.payment.activities.CheckoutCompleteActivity
import com.yuno.payments.example.features.payment.activities.CheckoutLiteActivity
import com.yuno.payments.example.features.payment.activities.PaymentRenderActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initListeners()
    }

    private fun initListeners() {
        findViewById<Button>(R.id.button_add_payment).setOnClickListener {
            startActivity(Intent(this, EnrollmentLiteActivity::class.java))
        }

        findViewById<Button>(R.id.button_enrollment_render).setOnClickListener {
            startActivity(Intent(this, EnrollmentRenderActivity::class.java))
        }

        findViewById<Button>(R.id.button_payment_lite)
            .setOnClickListener {
                startActivity(Intent(this, CheckoutLiteActivity::class.java))
            }

        findViewById<Button>(R.id.button_payment_render).setOnClickListener {
            startActivity(Intent(this, PaymentRenderActivity::class.java))
        }

        findViewById<Button>(R.id.button_payment_complete)
            .setOnClickListener {
                val intent = Intent(this, CheckoutCompleteActivity::class.java)
                startActivity(intent)
            }
    }
}
