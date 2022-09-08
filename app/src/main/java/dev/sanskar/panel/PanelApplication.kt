package dev.sanskar.panel

import android.app.Application
import timber.log.Timber

class PanelApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}