package com.dzhucinski.seattleplaces

import android.app.Application
import com.dzhucinski.seattleplaces.di.appModule
import com.dzhucinski.seattleplaces.di.repositoryModule
import com.dzhucinski.seattleplaces.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Created by Denis Zhuchinski on 4/10/19.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule, repositoryModule, viewModelModule)
        }
    }
}