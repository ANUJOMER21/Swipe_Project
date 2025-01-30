package com.swipe.test_project.DI

import android.app.Application
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.swipe.test_project.Worker.NetworkChangeReceiver
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApp: Application() {


   private lateinit var networkChangeReceiver: NetworkChangeReceiver
    override fun onCreate() {
        super.onCreate()
        run_PendingUpload_worker()
        startKoin {
            androidContext(this@MyApp)
            modules(appModule)
        }
    }
    private fun run_PendingUpload_worker() {
        networkChangeReceiver = NetworkChangeReceiver()
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(networkChangeReceiver)
    }
}