package com.yuno.payments.example

import android.app.Application
import com.yuno.payments.core.Yuno
import com.yuno.payments.core.YunoConfig

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Yuno.initialize(this, "API_KEY", YunoConfig())
    }
}
