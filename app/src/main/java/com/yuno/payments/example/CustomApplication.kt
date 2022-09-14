package com.yuno.payments.example

import android.app.Application
import com.yuno.payments.core.Yuno

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Yuno.initialize(this, "API_KEY")
    }
}