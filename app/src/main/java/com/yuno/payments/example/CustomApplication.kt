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
            "staging_gAAAAABjoHVRZ4wcpDoBS5hZGzZPzbZdCwjCJVGGEcl9nOx7-nJK5CXDWWjZYlTUpnaDTtOapO-PAOB1eizpUmTlf9P0nzGIChThL5zuMAWVsJo2w8lAKJ-P1g8eLBOfBUTlnifecea9NuzMI9JGskTpHNzI4TGlljkj5a9ejt4NGg31hZJiDEA=",
            YunoConfig(
                cardFlow = CardFormType.ONE_STEP,
                saveCardEnabled = false,
            )
        )
    }
}
