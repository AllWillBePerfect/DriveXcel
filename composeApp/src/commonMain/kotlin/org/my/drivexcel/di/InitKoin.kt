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
import org.my.drivexcel.KtorClientServer
import org.my.drivexcel.ThemeViewModel
import org.my.drivexcel.V2
import org.my.drivexcel.V2.MdnsManager
import org.my.drivexcel.WebSocketsViewModel
import org.my.drivexcel.data.DevicesRepository
import org.my.drivexcel.data.EventDirectoryManager
import org.my.drivexcel.data.EventDirectoryRepository
import org.my.drivexcel.data.EventsDataSource
import org.my.drivexcel.data.ExcelDataSource
import org.my.drivexcel.data.FileDataSource
import org.my.drivexcel.data.FileUtils
import org.my.drivexcel.data.ImageDataSource
import org.my.drivexcel.data.KtorWebSocketClientsManager
import org.my.drivexcel.data.KtorWebSocketServer
import org.my.drivexcel.data.MetaDataSource
import org.my.drivexcel.data.v3.EventFileStore
import org.my.drivexcel.data.v3.EventPathResolver
import org.my.drivexcel.data.v3.ExcelParser
import org.my.drivexcel.data.v3.FolderIdProvider
import org.my.drivexcel.data.v3.TimeProvider
import org.my.drivexcel.data.v3.repositories.EventRepositoryImpl
import org.my.drivexcel.data.v3.utils.EventImageMapper
import org.my.drivexcel.domain.models.EventImageMapperV2
import org.my.drivexcel.domain.repositories.EventRepository
import org.my.drivexcel.domain.usecases.CreateEventUseCaseOld
import org.my.drivexcel.domain.usecases.GetEventsUseCaseOld
import org.my.drivexcel.ui.screens.addevent.AddEventViewModel
import org.my.drivexcel.ui.screens.connection.ConnectionViewModel
import org.my.drivexcel.ui.screens.home.OldHomeViewModel
import org.my.drivexcel.ui.screens.settings.SettingsViewModel
import org.my.drivexcel.utils.ActionsManager
import org.my.drivexcel.utils.LoginTypeFactory
import org.my.drivexcel.v4.base.infractructure.filestorage.LocalStorageProvider
import org.my.drivexcel.v4.base.infractructure.filestorage.StorageProvider
import org.my.drivexcel.v4.data.SerializableParser
import org.my.drivexcel.v4.datasource.exception.mapper.DataExceptionToDomainMapper
import org.my.drivexcel.v4.datasource.exception.mapper.LeaderUserDataToDomainMapper
import org.my.drivexcel.v4.datasource.sources.EventFileDataSource
import org.my.drivexcel.v4.domain.repository.DeleteEventRepository
import org.my.drivexcel.v4.domain.repository.GetEventRepository
import org.my.drivexcel.v4.domain.repository.GetEventsRepository
import org.my.drivexcel.v4.domain.repository.GetLeaderUsersUseCase
import org.my.drivexcel.v4.domain.repository.UpdateEventRepository
import org.my.drivexcel.v4.domain.usecase.CreateEventUseCase
import org.my.drivexcel.v4.domain.usecase.DeleteEventUseCase
import org.my.drivexcel.v4.domain.usecase.GetEventUseCase
import org.my.drivexcel.v4.domain.usecase.GetEventsUseCase
import org.my.drivexcel.v4.domain.usecase.SaveLeaderUsersUseCase
import org.my.drivexcel.v4.domain.usecase.UpdateEventUseCase
import org.my.drivexcel.v4.ui.screens.event.EventViewModel
import org.my.drivexcel.v4.ui.screens.event_editor.EventEditorViewModel
import org.my.drivexcel.v4.ui.screens.events.EventsViewModel
import org.my.drivexcel.v4.ui.screens.home.HomeViewModel
import org.my.drivexcel.v4.ui.screens.importt.ImportViewModel
import org.my.drivexcel.v4.ui.screens.users.UsersViewModel
import org.my.drivexcel.v4.ui.utils.SnackbarManager

fun initKoin(
    appDeclaration: KoinAppDeclaration = {},
    platformModules: List<Module> = emptyList(),
    platformAction: KoinApplication.() -> Unit
) = startKoin {
    appDeclaration()

    modules(
        viewModelModule +
                dataModule +
                utilsModule +
                platformModules
    )
    platformAction.invoke(this)
}


val viewModelModule = module {
    viewModelOf(::ThemeViewModel)
//    viewModel { ThemeViewModel(get(), get(named(AppLogger.CONSOLE_LOGGER))) }
    viewModelOf(::SettingsViewModel)
    viewModelOf(::OldHomeViewModel)
    viewModelOf(::AddEventViewModel)
    viewModelOf(::ConnectionViewModel)
    viewModelOf(::WebSocketsViewModel)

    //v4
    viewModelOf(::EventEditorViewModel)
    viewModelOf(::EventsViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::EventViewModel)
    viewModelOf(::UsersViewModel)
    viewModelOf(::ImportViewModel)
}

val dataModule = module {
    singleOf(EventsDataSource::Impl) { bind<EventsDataSource>() }
    singleOf(FileUtils::Impl) { bind<FileUtils>() }
    singleOf(EventDirectoryManager::Impl) { bind<EventDirectoryManager>() }


    //v2
    singleOf(EventDirectoryRepository::Impl) { bind<EventDirectoryRepository>() }
    singleOf(FileDataSource::Impl) { bind<FileDataSource>() }
    singleOf(ImageDataSource::Impl) { bind<ImageDataSource>() }
    singleOf(MetaDataSource::Impl) { bind<MetaDataSource>() }
    singleOf(ExcelDataSource::Impl) { bind<ExcelDataSource>() }


    singleOf(::KtorWebSocketClientsManager)
    singleOf(::DevicesRepository)
    singleOf(::KtorWebSocketServer)

    // v3
    singleOf(EventFileStore::Impl) { bind<EventFileStore>() }
    singleOf(ExcelParser::Impl) { bind<ExcelParser>() }
    singleOf(FolderIdProvider::UUIDImpl) { bind<FolderIdProvider>() }
    singleOf(EventPathResolver::Impl) { bind<EventPathResolver>() }
    singleOf(TimeProvider::Impl) { bind<TimeProvider>() }
    singleOf(::EventImageMapper)

    factory {
        Json {

        }
    }
    singleOf(::EventRepositoryImpl) { bind<EventRepository>() }
    singleOf(CreateEventUseCaseOld::Impl) { bind<CreateEventUseCaseOld>() }
    singleOf(GetEventsUseCaseOld::Impl) { bind<GetEventsUseCaseOld>() }

    // v4
    singleOf(SerializableParser::Impl) { bind<SerializableParser>() }
    singleOf(::LocalStorageProvider) { bind<StorageProvider>() }
    singleOf(EventFileDataSource::Impl) { bind<EventFileDataSource>() }

    singleOf(GetEventsRepository::Impl) { bind<GetEventsRepository>() }
    singleOf(GetEventRepository::Impl) { bind<GetEventRepository>() }
    singleOf(UpdateEventRepository::Impl) { bind<UpdateEventRepository>() }
    singleOf(DeleteEventRepository::Impl) { bind<DeleteEventRepository>() }
    singleOf(org.my.drivexcel.v4.domain.repository.EventRepository::Impl) { bind<org.my.drivexcel.v4.domain.repository.EventRepository>() }

//    singleOf(::CreateEventUseCase)
    singleOf(::CreateEventUseCase)
    singleOf(::GetEventsUseCase)
    singleOf(::GetEventUseCase)
    singleOf(::UpdateEventUseCase)
    singleOf(::DeleteEventUseCase)
    singleOf(::SaveLeaderUsersUseCase)
    singleOf(::GetLeaderUsersUseCase)

    // v4 mappers

    singleOf(::DataExceptionToDomainMapper)
    singleOf(::LeaderUserDataToDomainMapper)
    singleOf(::EventImageMapperV2)

    // v4 ui utils
    singleOf(::SnackbarManager)


}

val utilsModule = module {
    singleOf(::LoginTypeFactory)
    singleOf(::ActionsManager)
    singleOf(::KtorClientServer)



    factory {
        get<V2.MdnsServiceBuilderProvider>().provide()
    }
    factory {
        get<V2.MdnsServiceBuilder>().build()
    }
    singleOf(::MdnsManager)

    factory {
        get<V2.KtorClientProvider>().client
    }
}
