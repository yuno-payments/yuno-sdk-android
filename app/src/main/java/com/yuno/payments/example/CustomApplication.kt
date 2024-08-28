package com.yuno.payments.example

import android.app.Application
import com.yuno.payments.core.Yuno
import com.yuno.payments.core.YunoConfig

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Yuno.initialize(this, "staging_gAAAAABjsuEyvM25OAvZfUfzjamrzIWyPliMb2Kh3lPo99KzD1eaRallyXoIpkoz2VBs7cU3WaXJplPc5h8nX1OQFrjyjvM9Vfuxy9Rv_Y_0xMImkeWocPOkGaqo3h-rQRc5DqG7gbv3cg3EXJnhsUXB4layabSwrCgEzfS2lbBLoWCF1MjGHXI=", YunoConfig())
    }
}
