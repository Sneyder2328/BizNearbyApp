package com.sneyder.biznearby

import android.os.StrictMode
import com.facebook.stetho.Stetho
import timber.log.Timber

class BizNearByApp : BaseApp() {

    override fun onCreate() {
        setStrictMode()
        super.onCreate()
        initTimber()
        Stetho.initializeWithDefaults(this)
    }

    private fun initTimber() {
        Timber.plant(MyCustomDebugTree())
    }

    class MyCustomDebugTree: Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String? {
            return "${super.createStackElementTag(element)}:${element.lineNumber}"
        }
    }

    /**
     * Set up strict mode for debugging
     * Note: do not enable penaltyDeath because it cause some false positives that shoot down the app
     */
    private fun setStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                //.penaltyDeath()
                .build())
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                //.penaltyDeath()
                .build())
    }

}