package com.yuno.payments.example.ui.enrollment

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.yuno.payments.example.BuildConfig
import com.yuno.payments.example.R
import com.yuno.payments.example.ui.views.CustomEditText
import com.yuno.payments.features.enrollment.initEnrollment
import com.yuno.payments.features.enrollment.startEnrollment

class EnrollPaymentActivity : AppCompatActivity() {

    private lateinit var editTextCustomerSession: CustomEditText
    private lateinit var editTextCountryCode: CustomEditText
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enroll_payment)
        initViews()
        initEnrollment(::onEnrollmentStateChange)
    }

    private fun initViews() {
        editTextCustomerSession = findViewById(R.id.editText_customer)
        editTextCountryCode = findViewById(R.id.editText_code)
        button = findViewById(R.id.button_add_payment)
        initListeners()
    }

    private fun initListeners() {
        button.setOnClickListener { initEnrollment() }
    }

    private fun initEnrollment() {
        if (editTextCustomerSession.isValid) {
            startEnrollment(
                customerSession = editTextCustomerSession.text.toString(),
                countryCode = editTextCountryCode.text.toString()
            )
        }
    }

    private fun onEnrollmentStateChange(enrollmentState: String?) {
        enrollmentState?.let {
            Log.e("Enrollment State", it)
        }
    }
}