package com.bystrov.sto

import android.app.Application
import com.bystrov.sto.di.initKoin
import org.koin.android.ext.koin.androidContext

class StoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@StoApplication)
        }
    }
}
