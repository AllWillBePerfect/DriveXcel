package org.my.drivexcel

import android.app.Application
import android.net.wifi.WifiManager
import org.my.drivexcel.di.initKoinWithModules

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoinWithModules()
        val wifi = this.getSystemService(WIFI_SERVICE) as WifiManager
        val lock = wifi.createMulticastLock("myapp-lock").apply { acquire() }

    }

}





