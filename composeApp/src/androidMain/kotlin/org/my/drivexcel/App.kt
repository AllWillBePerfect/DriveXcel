package org.my.drivexcel

import android.app.Application
import org.my.drivexcel.di.initKoinWithModules

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoinWithModules()
    }

}


