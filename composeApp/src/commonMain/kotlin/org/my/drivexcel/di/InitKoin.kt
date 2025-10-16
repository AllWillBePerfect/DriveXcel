package org.my.drivexcel.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.my.drivexcel.ThemeViewModel
import org.my.drivexcel.data.EventsDataSource
import org.my.drivexcel.ui.screens.home.HomeViewModel
import org.my.drivexcel.ui.screens.settings.SettingsViewModel

fun initKoin(
    appDeclaration: KoinAppDeclaration = {},
    platformModules: List<Module> = emptyList(),
    platformAction: KoinApplication.() -> Unit
) = startKoin {
    appDeclaration()

    modules(
        viewModelModule +
                dataModule +
                platformModules
    )
    platformAction.invoke(this)
}


val viewModelModule = module {
    viewModelOf(::ThemeViewModel)
//    viewModel { ThemeViewModel(get(), get(named(AppLogger.CONSOLE_LOGGER))) }
    viewModelOf(::SettingsViewModel)
    viewModelOf(::HomeViewModel)
}

val dataModule = module {
    singleOf(EventsDataSource::Impl) { bind<EventsDataSource>() }
}

