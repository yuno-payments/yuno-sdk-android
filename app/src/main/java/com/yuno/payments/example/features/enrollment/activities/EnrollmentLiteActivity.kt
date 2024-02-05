package com.yuno.payments.example.features.enrollment.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yuno.payments.example.BuildConfig
import com.yuno.payments.example.R
import com.yuno.payments.example.ui.views.CustomEditText
import com.yuno.payments.features.enrollment.initEnrollment
import com.yuno.payments.features.enrollment.startEnrollment

class EnrollmentLiteActivity : AppCompatActivity() {

    private lateinit var editTextCustomerSession: CustomEditText
    private lateinit var editTextCountryCode: CustomEditText
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrollment_lite)
        initViews()
        initEnrollment(::onEnrollmentStateChange)
        intent.data?.let {
            if (it.toString().contains("yuno://www.y.uno/enrollment")) {
                startEnrollment(
                    customerSession = getCustomerSession(it.toString()),
                    countryCode = editTextCountryCode.text.toString().uppercase(),
                )
            }
        }
    }

    private fun getCustomerSession(uri: String): String {
        return uri.substringAfter("yuno://www.y.uno/enrollment?customerSession=")
    }

    private fun initViews() {
        editTextCustomerSession = findViewById(R.id.editText_customer)
        editTextCountryCode = findViewById(R.id.editText_code)
        editTextCustomerSession.setText(BuildConfig.YUNO_TEST_CUSTOMER_SESSION)
        editTextCountryCode.setText(BuildConfig.YUNO_TEST_COUNTRY_CODE)
        button = findViewById(R.id.button_add_payment)
        initListeners()
    }

    private fun initListeners() {
        button.setOnClickListener { startEnrollment() }
    }

    private fun startEnrollment() {
        if (editTextCustomerSession.isValid && editTextCountryCode.isValid) {
            startEnrollment(
                customerSession = editTextCustomerSession.text.toString(),
                countryCode = editTextCountryCode.text.toString().uppercase()
            )
        }
    }

    private fun onEnrollmentStateChange(enrollmentState: String?) {
        enrollmentState?.let {
            Log.e("Enrollment State", it)
        }
    }
}
