package org.my.drivexcel.di

import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.my.drivexcel.ThemeViewModel
import org.my.drivexcel.base.domain.dispatcher.AppDispatchers
import org.my.drivexcel.platform.FolderIdProvider
import org.my.drivexcel.platform.TimeProvider
import org.my.drivexcel.base.infractructure.datastore.DatastoreProvider
import org.my.drivexcel.infrastructure.LocalStorageProvider
import org.my.drivexcel.base.infractructure.filestorage.StorageProvider
import org.my.drivexcel.data.SerializableParser
import org.my.drivexcel.data.mappers.LeaderUserDataToDomainMapper
import org.my.drivexcel.data.parser.ExcelParserImpl
import org.my.drivexcel.data.repositories.EventRepositoryImpl
import org.my.drivexcel.datasource.exception.mapper.DataExceptionToDomainMapper
import org.my.drivexcel.datasource.sources.EventFileDataSource
import org.my.drivexcel.datasource.sources.EventMetaDataSource
import org.my.drivexcel.datasource.sources.HistoryDataSource
import org.my.drivexcel.datasource.sources.PreferencesDataSource
import org.my.drivexcel.datasource.sources.UsersDataSource
import org.my.drivexcel.domain.parser.EventImageMapperV2
import org.my.drivexcel.domain.parser.ExcelParser
import org.my.drivexcel.domain.repository.EventRepository
import org.my.drivexcel.domain.usecase.CreateEventUseCase
import org.my.drivexcel.domain.usecase.DeleteEventUseCase
import org.my.drivexcel.domain.usecase.GetEventUseCase
import org.my.drivexcel.domain.usecase.ObserveEventUseCase
import org.my.drivexcel.domain.usecase.ObserveEventsUseCase
import org.my.drivexcel.domain.usecase.ObserveLeaderUsersUseCase
import org.my.drivexcel.domain.usecase.SaveLeaderUsersUseCase
import org.my.drivexcel.domain.usecase.UpdateEventUseCase
import org.my.drivexcel.ui.navigation.AppViewModel
import org.my.drivexcel.ui.screens.event.EventViewModel
import org.my.drivexcel.ui.screens.event_editor.EventEditorViewModel
import org.my.drivexcel.ui.screens.events.EventsViewModel
import org.my.drivexcel.ui.screens.home.HomeViewModel
import org.my.drivexcel.ui.screens.importt.ImportViewModel
import org.my.drivexcel.domain.demo.EventDemoDataGenerator
import org.my.drivexcel.ui.screens.login.LoginViewModel
import org.my.drivexcel.ui.screens.settings.SettingsViewModel
import org.my.drivexcel.ui.screens.users.UsersViewModel
import org.my.drivexcel.ui.utils.SnackbarManager

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


    //v4
    viewModelOf(::EventEditorViewModel)
    viewModelOf(::EventsViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::EventViewModel)
    viewModelOf(::UsersViewModel)
    viewModelOf(::ImportViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::AppViewModel)
    viewModelOf(::LoginViewModel)
}

val dataModule = module {


    // v3
    singleOf(FolderIdProvider::UUIDImpl) { bind<FolderIdProvider>() }
    singleOf(TimeProvider::Impl) { bind<TimeProvider>() }

    factory {
        Json {

        }
    }


    // v4
    singleOf(SerializableParser::Impl) { bind<SerializableParser>() }
    singleOf(::LocalStorageProvider) { bind<StorageProvider>() }
    singleOf(EventFileDataSource::Impl) { bind<EventFileDataSource>() }

    singleOf(::EventRepositoryImpl) { bind<EventRepository>() }

    factory {
        get<DatastoreProvider>().provide { "preferences.preferences_pb" }
    }
    singleOf(PreferencesDataSource::Impl) { bind<PreferencesDataSource>() }

//    singleOf(::CreateEventUseCase)
    singleOf(::CreateEventUseCase)
    singleOf(::ObserveEventsUseCase)
    singleOf(::GetEventUseCase)
    singleOf(::UpdateEventUseCase)
    singleOf(::DeleteEventUseCase)
    singleOf(::SaveLeaderUsersUseCase)
    singleOf(::ObserveLeaderUsersUseCase)
    singleOf(::ObserveEventUseCase)

    // v4 mappers

    singleOf(::DataExceptionToDomainMapper)
    singleOf(::LeaderUserDataToDomainMapper)
    singleOf(::EventImageMapperV2)

    // v4 ui utils
    singleOf(::SnackbarManager)


    singleOf(UsersDataSource::Impl) { bind<UsersDataSource>() }
    singleOf(HistoryDataSource::Impl) { bind<HistoryDataSource>() }
    singleOf(EventMetaDataSource::Impl) { bind<EventMetaDataSource>() }
    singleOf(::ExcelParserImpl) { bind<ExcelParser>() }

    singleOf(AppDispatchers::Impl) { bind<AppDispatchers>() }

    singleOf(::EventDemoDataGenerator)


}

