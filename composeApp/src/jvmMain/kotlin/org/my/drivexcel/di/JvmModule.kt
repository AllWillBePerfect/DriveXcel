package org.my.drivexcel.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.my.drivexcel.platform.utils.AppLoggerJvm
import org.my.drivexcel.platform.utils.AppLoggerWithLocalFileJvm
import org.my.drivexcel.platform.utils.BackHandlerProviderJvm
import org.my.drivexcel.platform.ImagePickerProvider
import org.my.drivexcel.platform.utils.ImagePickerProviderJvm
import org.my.drivexcel.platform.MyWindowSizeClass
import org.my.drivexcel.platform.utils.MyWindowSizeClassJvm
import org.my.drivexcel.platform.PlatformProvider
import org.my.drivexcel.platform.utils.PlatformProviderJvm
import org.my.drivexcel.ui.BoxDividerProviderJvm
import org.my.drivexcel.v3.RootDirPathProviderJvm
import org.my.drivexcel.base.infractructure.datastore.DatastoreProvider
import org.my.drivexcel.platform.AppLogger
import org.my.drivexcel.platform.BackHandlerProvider
import org.my.drivexcel.platform.RootDirPathProvider
import org.my.drivexcel.ui.platform.BoxDividerProvider
import org.my.drivexcel.ui.platform.ClipboardManager
import org.my.drivexcel.ui.platform.XlsFilePicker
import org.my.drivexcel.ui.utils.nav.WindowSizeClassProvider
import v4.platform.ClipboardManagerJvm
import v4.platform.DatastoreProviderJvm
import v4.platform.WindowSizeClassProviderJvm
import v4.platform.XlsFilePickerJvm

fun initKoinWithModules() = initKoin(
    platformModules = listOf(
        jvmModule
    )
) { }

val jvmModule = module {




    // Базовый консольный логгер
    single<AppLoggerJvm> { AppLoggerJvm() }

    // Консольный логгер (named)
    single<AppLogger>(named(AppLogger.CONSOLE_LOGGER)) { get<AppLoggerJvm>() }

    // Консоль + файл (named) — можно оставить для явного выбора
    single<AppLogger>(named(AppLogger.CONSOLE_AND_LOCAL_FILE_LOGGER)) { AppLoggerWithLocalFileJvm(get<AppLoggerJvm>()) }

    // Default AppLogger = console + file
    single<AppLogger> { get<AppLogger>(named(AppLogger.CONSOLE_AND_LOCAL_FILE_LOGGER)) }

    singleOf(::PlatformProviderJvm) {bind<PlatformProvider>()}
    singleOf(::MyWindowSizeClassJvm) {bind<MyWindowSizeClass>()}
    singleOf(::BackHandlerProviderJvm) {bind<BackHandlerProvider>()}
    singleOf(::ImagePickerProviderJvm) {bind<ImagePickerProvider>()}

    // ui
    singleOf(::BoxDividerProviderJvm) {bind<BoxDividerProvider>()}

    // v3
    singleOf(::RootDirPathProviderJvm) {bind<RootDirPathProvider>()}

    //v4
    singleOf(::XlsFilePickerJvm) { bind<XlsFilePicker>() }
    singleOf(::WindowSizeClassProviderJvm) { bind<WindowSizeClassProvider>() }
    singleOf(::DatastoreProviderJvm) { bind<DatastoreProvider>() }
    singleOf(::ClipboardManagerJvm) { bind<ClipboardManager>() }

}
