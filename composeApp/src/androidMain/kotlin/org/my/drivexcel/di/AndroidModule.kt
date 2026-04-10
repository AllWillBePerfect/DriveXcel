package org.my.drivexcel.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.my.drivexcel.base.infractructure.datastore.DatastoreProvider
import org.my.drivexcel.platform.AppLogger
import org.my.drivexcel.platform.BackHandlerProvider
import org.my.drivexcel.platform.ImagePickerProvider
import org.my.drivexcel.platform.MyWindowSizeClass
import org.my.drivexcel.platform.PlatformProvider
import org.my.drivexcel.platform.RootDirPathProvider
import org.my.drivexcel.platform.utils.AppLoggerAndroid
import org.my.drivexcel.platform.utils.BackHandlerProviderAndroid
import org.my.drivexcel.platform.utils.ImagePickerProviderAndroid
import org.my.drivexcel.platform.utils.MyWindowSizeClassAndroid
import org.my.drivexcel.platform.utils.PlatformProviderAndroid
import org.my.drivexcel.ui.BoxDividerProviderAndroid
import org.my.drivexcel.ui.platform.BoxDividerProvider
import org.my.drivexcel.ui.platform.ClipboardManager
import org.my.drivexcel.ui.platform.XlsFilePicker
import org.my.drivexcel.ui.utils.nav.WindowSizeClassProvider
import org.my.drivexcel.v3.RootDirPathProviderAndroid
import org.my.drivexcel.v4.platform.ClipboardManagerAndroid
import org.my.drivexcel.v4.platform.DatastoreProviderAndroid
import org.my.drivexcel.v4.platform.WindowSizeClassProviderAndroid
import org.my.drivexcel.v4.platform.XlsFilePickerAndroid


fun Application.initKoinWithModules() = initKoin(
    platformModules = listOf(
        androidModule
    )
) { androidContext(this@initKoinWithModules) }

val androidModule = module {


    // platform/utils
    singleOf(::AppLoggerAndroid) { bind<AppLogger>() }
    singleOf(::PlatformProviderAndroid) { bind<PlatformProvider>() }
    singleOf(::MyWindowSizeClassAndroid) { bind<MyWindowSizeClass>() }
    singleOf(::BackHandlerProviderAndroid) { bind<BackHandlerProvider>() }
    singleOf(::ImagePickerProviderAndroid) { bind<ImagePickerProvider>() }





    // ui
    singleOf(::BoxDividerProviderAndroid) { bind<BoxDividerProvider>() }

    //v3
    singleOf(::RootDirPathProviderAndroid) { bind<RootDirPathProvider>() }

    //v4
    singleOf(::XlsFilePickerAndroid) { bind<XlsFilePicker>() }
    singleOf(::WindowSizeClassProviderAndroid) { bind<WindowSizeClassProvider>() }
    singleOf(::DatastoreProviderAndroid) { bind<DatastoreProvider>() }
    singleOf(::ClipboardManagerAndroid) { bind<ClipboardManager>() }
}