package com.yuno.payments.example.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yuno.payments.core.enrollment.startYunoEnrollment
import com.yuno.payments.example.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startYunoEnrollment("session_id", "payment_method")
    }
}