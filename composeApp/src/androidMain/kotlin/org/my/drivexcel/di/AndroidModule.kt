package org.my.drivexcel.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.my.drivexcel.platform.datasources.SettingsDataSource
import org.my.drivexcel.platform.datasources.SettingsDataSourceAndroid
import org.my.drivexcel.platform.utils.AppLogger
import org.my.drivexcel.platform.utils.AppLoggerAndroid
import org.my.drivexcel.platform.utils.BackHandlerProvider
import org.my.drivexcel.platform.utils.BackHandlerProviderAndroid
import org.my.drivexcel.platform.utils.PlatformProvider
import org.my.drivexcel.platform.utils.PlatformProviderAndroid
import org.my.drivexcel.platform.utils.WindowSizeClass
import org.my.drivexcel.platform.utils.WindowSizeClassAndroid


fun Application.initKoinWithModules() = initKoin(
    platformModules = listOf(
        androidModule
    )
) { androidContext(this@initKoinWithModules) }

val androidModule = module {

    // dataSources
    singleOf(::SettingsDataSourceAndroid) { bind<SettingsDataSource>() }

    //utils
    singleOf(::AppLoggerAndroid) { bind<AppLogger>() }
    singleOf(::PlatformProviderAndroid) { bind<PlatformProvider>() }
    singleOf(::WindowSizeClassAndroid) {bind<WindowSizeClass>()}
    singleOf(::BackHandlerProviderAndroid) {bind<BackHandlerProvider>()}
}