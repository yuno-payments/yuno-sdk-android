package com.yuno.payments.example.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.yuno.payments.core.Yuno
import com.yuno.payments.example.R

class MainActivity : AppCompatActivity() {

    private lateinit var buttonSetApiKey: Button
    private lateinit var buttonEnrollmentFlow: Button
    private lateinit var buttonPaymentFlow: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        buttonSetApiKey = findViewById(R.id.button_change_api_key)
        buttonEnrollmentFlow = findViewById(R.id.button_start_enrollment_flow)
        buttonPaymentFlow = findViewById(R.id.button_start_payment_flow)
        setListeners()
    }

    private fun setListeners() {
        buttonSetApiKey.setOnClickListener {
            Yuno.initialize(
                this.application,
                findViewById<EditText>(R.id.editText_api_key).text.toString()
            )
        }

        buttonPaymentFlow.setOnClickListener {
            startActivity(
                Intent(this, PaymentFlowActivity::class.java)
            )
        }
    }
}