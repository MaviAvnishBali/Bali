package com.bali.android

import android.app.Application
import com.bali.android.di.androidModule
import com.bali.shared.config.AppConfig
import com.bali.shared.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class BaliApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@BaliApplication)
            modules(androidModule, module {
                single { AppConfig(baseUrl = com.bali.shared.BuildConfig.BASE_URL) }
            })
        }
    }
}
