package org.my.drivexcel.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.my.drivexcel.CoroutineDispatcherProvider
import org.my.drivexcel.CoroutineDispatcherProviderAndroid
import org.my.drivexcel.KtorClientProviderAndroid
import org.my.drivexcel.KtorHttpPlatformProvider
import org.my.drivexcel.KtorHttpPlatformProviderAndroid
import org.my.drivexcel.LocalIpAddress
import org.my.drivexcel.LocalIpAddressAndroid
import org.my.drivexcel.MdnsManagerProvider
import org.my.drivexcel.MdnsManagerProviderAndroid
import org.my.drivexcel.MdnsServiceBuilderProviderAndroid
import org.my.drivexcel.V2
import org.my.drivexcel.data.v3.RootDirPathProvider
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
import org.my.drivexcel.v3.RootDirPathProviderAndroid
import org.my.drivexcel.v4.module.XlsFilePickerAndroid
import org.my.drivexcel.v4.ui.module.XlsFilePicker


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
    singleOf(::KtorHttpPlatformProviderAndroid) { bind<KtorHttpPlatformProvider>() }
    singleOf(::KtorHttpPlatformProviderAndroid) { bind<KtorHttpPlatformProvider>() }
    singleOf(::MdnsManagerProviderAndroid) { bind<MdnsManagerProvider>() }
    singleOf(::LocalIpAddressAndroid) { bind<LocalIpAddress>() }



    singleOf(::CoroutineDispatcherProviderAndroid) { bind<CoroutineDispatcherProvider>() }
    singleOf(::MdnsServiceBuilderProviderAndroid) { bind<V2.MdnsServiceBuilderProvider>() }
    singleOf(::KtorClientProviderAndroid) { bind<V2.KtorClientProvider>() }


    // ui
    singleOf(::BoxDividerProviderAndroid) { bind<BoxDividerProvider>() }

    //v3
    singleOf(::RootDirPathProviderAndroid) { bind<RootDirPathProvider>() }

    //v4
    singleOf(::XlsFilePickerAndroid) { bind<XlsFilePicker>() }
}