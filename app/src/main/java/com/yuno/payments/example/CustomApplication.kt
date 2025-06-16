package com.yuno.payments.example

import android.app.Application
import com.yuno.sdk.Yuno
import com.yuno.sdk.YunoConfig

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Yuno.initialize(this, "API_KEY", YunoConfig())
    }
}
