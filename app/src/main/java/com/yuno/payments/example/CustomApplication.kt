package com.yuno.payments.example

import android.app.Application
import com.yuno.payments.core.Yuno
import com.yuno.payments.core.YunoConfig
import com.yuno.payments.features.base.ui.screens.CardFormType

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Yuno.initialize(this, "API_KEY", config = YunoConfig(
            cardFlow = CardFormType.MULTI_STEP,
            saveCardEnabled = true
        ))
    }
}