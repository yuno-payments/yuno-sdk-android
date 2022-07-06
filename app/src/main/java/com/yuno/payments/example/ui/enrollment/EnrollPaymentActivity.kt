package com.yuno.payments.example.ui.enrollment

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.yuno.payments.example.R
import com.yuno.payments.features.enrollment.startEnrollment

class EnrollPaymentActivity : AppCompatActivity() {

    private lateinit var buttonSetCustomerSession: Button

    private lateinit var editTextCustomerSession: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enroll_payment)
        initViews()
    }

    private fun initViews() {
        buttonSetCustomerSession = findViewById(R.id.button_set_customer_session)
        editTextCustomerSession = findViewById(R.id.editText_customer_session)
        buttonSetCustomerSession.setOnClickListener {
            startEnrollment(
                customerSession = editTextCustomerSession.text.toString()
            )
        }
    }
}