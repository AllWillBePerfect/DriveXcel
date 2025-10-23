package org.my.drivexcel.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.my.drivexcel.platform.datasources.DirectoriesDataSource
import org.my.drivexcel.platform.datasources.DirectoriesDataSourceAndroid
import org.my.drivexcel.platform.datasources.SettingsDataSource
import org.my.drivexcel.platform.datasources.SettingsDataSourceAndroid
import org.my.drivexcel.platform.utils.AppLogger
import org.my.drivexcel.platform.utils.AppLoggerAndroid
import org.my.drivexcel.platform.utils.BackHandlerProvider
import org.my.drivexcel.platform.utils.BackHandlerProviderAndroid
import org.my.drivexcel.platform.utils.DirectoryPathProvider
import org.my.drivexcel.platform.utils.DirectoryPathProviderAndroid
import org.my.drivexcel.platform.utils.ImageConverter
import org.my.drivexcel.platform.utils.ImageConverterAndroid
import org.my.drivexcel.platform.utils.ImagePicker
import org.my.drivexcel.platform.utils.ImagePickerAndroid
import org.my.drivexcel.platform.utils.PlatformProvider
import org.my.drivexcel.platform.utils.PlatformProviderAndroid
import org.my.drivexcel.platform.utils.WindowSizeClass
import org.my.drivexcel.platform.utils.WindowSizeClassAndroid
import org.my.drivexcel.platform.utils.XlsReader
import org.my.drivexcel.platform.utils.XlsReaderAndroid
import org.my.drivexcel.ui.BoxDividerProviderAndroid
import org.my.drivexcel.ui.screens.home.platform.BoxDividerProvider


fun Application.initKoinWithModules() = initKoin(
    platformModules = listOf(
        androidModule
    )
) { androidContext(this@initKoinWithModules) }

val androidModule = module {

    // dataSources
    singleOf(::SettingsDataSourceAndroid) { bind<SettingsDataSource>() }
    singleOf(::DirectoriesDataSourceAndroid) { bind<DirectoriesDataSource>() }

    // platform/utils
    singleOf(::AppLoggerAndroid) { bind<AppLogger>() }
    singleOf(::PlatformProviderAndroid) { bind<PlatformProvider>() }
    singleOf(::WindowSizeClassAndroid) { bind<WindowSizeClass>() }
    singleOf(::BackHandlerProviderAndroid) { bind<BackHandlerProvider>() }
    singleOf(::ImagePickerAndroid) { bind<ImagePicker>() }
    singleOf(::ImageConverterAndroid) { bind<ImageConverter>() }
    singleOf(::XlsReaderAndroid) { bind<XlsReader>() }
    singleOf(::DirectoryPathProviderAndroid) { bind<DirectoryPathProvider>() }


    // ui
    singleOf(::BoxDividerProviderAndroid) { bind<BoxDividerProvider>() }
}