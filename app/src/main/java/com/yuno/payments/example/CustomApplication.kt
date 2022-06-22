package com.yuno.payments.example

import android.app.Application
import com.yuno.payments.core.Yuno

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Yuno.initialize(this, "prod_gAAAAABiq1BM0un867cfl2lDn2vJHYVQodf2oQgJSX8UGzsoQhz1PtgV4hYFc-LGqt3Ho0BfaN3vMH07G9g-2H3kLGIUrGd6cO2mONeXVV13FoN9Em_k52wcr21366caJ1fX9q-zJCc1lghemfFewuDAmY1Z0ZW3ALLEhGPlRHZnpp4WvqjcpKs=")
    }
}