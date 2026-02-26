package com.yuno.payments.example

import android.app.Application
import com.yuno.presentation.core.card.CardFormType
import com.yuno.sdk.Yuno
import com.yuno.sdk.YunoConfig

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Yuno.initialize(
            this, 
            "YOUR_API_KEY",
            YunoConfig(
                cardFlow = CardFormType.ONE_STEP,
                saveCardEnabled = false,
            )
        )
    }
}
