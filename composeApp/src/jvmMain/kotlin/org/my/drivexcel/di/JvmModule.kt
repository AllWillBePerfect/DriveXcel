package org.my.drivexcel.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.my.drivexcel.platform.datasources.DirectoriesDataSource
import org.my.drivexcel.platform.datasources.DirectoriesDataSourceJvm
import org.my.drivexcel.platform.datasources.SettingsDataSource
import org.my.drivexcel.platform.datasources.SettingsDataSourceJvm
import org.my.drivexcel.platform.utils.AppLogger
import org.my.drivexcel.platform.utils.AppLoggerJvm
import org.my.drivexcel.platform.utils.AppLoggerWithLocalFileJvm
import org.my.drivexcel.platform.utils.BackHandlerProvider
import org.my.drivexcel.platform.utils.BackHandlerProviderJvm
import org.my.drivexcel.platform.utils.ImageConverter
import org.my.drivexcel.platform.utils.ImageConverterJvm
import org.my.drivexcel.platform.utils.ImagePicker
import org.my.drivexcel.platform.utils.ImagePickerJvm
import org.my.drivexcel.platform.utils.PlatformProvider
import org.my.drivexcel.platform.utils.PlatformProviderJvm
import org.my.drivexcel.platform.utils.WindowSizeClass
import org.my.drivexcel.platform.utils.WindowSizeClassJvm
import org.my.drivexcel.platform.utils.XlsReader
import org.my.drivexcel.platform.utils.XlsReaderJvm
import org.my.drivexcel.ui.BoxDividerProviderJvm
import org.my.drivexcel.ui.screens.home.platform.BoxDividerProvider

fun initKoinWithModules() = initKoin(
    platformModules = listOf(
        jvmModule
    )
) { }

val jvmModule = module {

    // dataSources
    singleOf(::SettingsDataSourceJvm) { bind<SettingsDataSource>() }
    singleOf(::DirectoriesDataSourceJvm) { bind<DirectoriesDataSource>() }

    //utils
//    singleOf(::AppLoggerJvm) {bind<AppLogger>()}

//    single<AppLogger>(named(AppLogger.CONSOLE_LOGGER)) { AppLoggerJvm() }
//    single<AppLogger>(named(AppLogger.CONSOLE_AND_LOCAL_FILE_LOGGER)) {
//        AppLoggerWithLocalFileJvm(
//            get(
//                named(
//                    AppLogger.CONSOLE_LOGGER
//                )
//            )
//        )
//    }

    // 1️⃣ Базовый консольный логгер
    single<AppLoggerJvm> { AppLoggerJvm() }

    // 2️⃣ Консольный логгер (named)
    single<AppLogger>(named(AppLogger.CONSOLE_LOGGER)) { get<AppLoggerJvm>() }

    // 3️⃣ Консоль + файл (named) — можно оставить для явного выбора
    single<AppLogger>(named(AppLogger.CONSOLE_AND_LOCAL_FILE_LOGGER)) { AppLoggerWithLocalFileJvm(get<AppLoggerJvm>()) }

    // 4️⃣ Default AppLogger = console + file
    single<AppLogger> { get<AppLogger>(named(AppLogger.CONSOLE_AND_LOCAL_FILE_LOGGER)) }

    singleOf(::PlatformProviderJvm) {bind<PlatformProvider>()}
    singleOf(::WindowSizeClassJvm) {bind<WindowSizeClass>()}
    singleOf(::BackHandlerProviderJvm) {bind<BackHandlerProvider>()}
    singleOf(::ImagePickerJvm) {bind<ImagePicker>()}
    singleOf(::ImageConverterJvm) {bind<ImageConverter>()}
    singleOf(::XlsReaderJvm) {bind<XlsReader>()}

    //ui
    singleOf(::BoxDividerProviderJvm) {bind<BoxDividerProvider>()}
}
