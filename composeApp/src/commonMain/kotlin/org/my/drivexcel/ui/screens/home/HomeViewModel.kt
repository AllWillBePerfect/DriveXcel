package org.my.drivexcel.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.my.drivexcel.data.EventDirectoryManager
import org.my.drivexcel.data.EventsDataSource
import org.my.drivexcel.platform.datasources.DirectoriesDataSource
import org.my.drivexcel.platform.utils.ImageConverter
import org.my.drivexcel.platform.utils.LeaderUser
import org.my.drivexcel.platform.utils.XlsReader

class HomeViewModel(
    private val eventsDataSource: EventsDataSource,
    private val directoriesDataSource: DirectoriesDataSource,
    private val xlsReader: XlsReader,
    private val imageConverter: ImageConverter,
    private val eventDirectoryManager: EventDirectoryManager
) : ViewModel() {

    init {
        println("HomeViewModel init")
    }

    private val viewModelState = MutableStateFlow(
        HomeViewModelState()
    )

    // Поток, который реагирует на смену selectedEventId
    private val selectedUsersJob = viewModelState
        .map { it.selectedEventId }
        .distinctUntilChanged()
        .onEach { selectedId ->
            viewModelState.update { it.copy(isDetailsLoading = true) }
            val users = if (selectedId == null) {
                emptyList()
            } else {
                val path = directoriesDataSource.findExcelFilePathByEventName(selectedId)
                path?.let { xlsReader.readExcel(it) } ?: emptyList()
            }

            viewModelState.update { it.copy(leaderUsers = users, isDetailsLoading = false) }
        }
        .flowOn(Dispatchers.IO)
        .launchIn(viewModelScope)


    val uiState: StateFlow<HomeUiState> = combine(
        viewModelState,
        eventDirectoryManager.events,
    ) { state, events ->
        val homeEvents = events.map {
            HomeEvent(
                id = it.id,
                name = it.name,
                isSelected = it.id == state.selectedEventId,
                imagePath = it.imagePath
            )
        }
//        val selectedEvent = homeEvents.find { it.id == state.selectedEventId }
        state.copy(
            eventsList = homeEvents
        ).toUiState()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeViewModelState().toUiState()
    )


    fun onEventClick(eventId: String) {
        viewModelState.update {
            it.copy(
                isDetailsOpen = true,
                selectedEventId = eventId
            )
        }
    }

    fun onEventClose() {
        viewModelState.update {
            it.copy(
                isDetailsOpen = false,
                selectedEventId = null
            )
        }
    }

    fun onEventDelete(eventId: String) {
        viewModelScope.launch { eventDirectoryManager.deleteEventDir(eventId) }
    }

    fun switchTab(position: Int) =
        viewModelState.update {
            it.copy(
                selectedTabOnDetails = position
            )
        }


    data class HomeViewModelState(
        val eventsList: List<HomeEvent>? = null,
        val isDetailsOpen: Boolean = false,
        val selectedEventId: String? = null,
        val leaderUsers: List<LeaderUser>? = null,
        val isDetailsLoading: Boolean = false,
        val selectedTabOnDetails: Int = 0,
    ) {

        fun toUiState(): HomeUiState {
            println("HomeViewModelState $selectedTabOnDetails")

            if (eventsList == null) {
                return HomeUiState.Loading
            } else {
                val selected = eventsList.find { it.id == selectedEventId }
                val detailsOpen = selected != null && isDetailsOpen
                val users = leaderUsers ?: emptyList()

                return HomeUiState.Loaded(
                    eventsHome = eventsList,
                    isDetailsOpen = detailsOpen,
                    selectedEvent = selected
                        ?.let { homeEvent ->
                            EventOnDetailsUiState.EventSelected(
                                isLoading = isDetailsLoading,
                                selectedTabOnDetails = selectedTabOnDetails,
                                eventId = homeEvent.id,
                                eventName = homeEvent.name,
                                users = users
                            )
                        }
                        ?: EventOnDetailsUiState.NoEventSelected(
                            isLoading = isDetailsLoading,
                            selectedTabOnDetails = selectedTabOnDetails,
                        )
                )
            }

        }

        /*private fun toDetailsUsersUiState(): DetailsUsersUiState {
            return if (leaderUsers == null) {
                DetailsUsersUiState.NoFile(
                    isLoading = isDetailsLoading,
                    selectedEventId = selectedEventId
                )
            } else {
                DetailsUsersUiState.HasFile(
                    isLoading = isDetailsLoading,
                    users = leaderUsers
                )
            }
        }*/
    }

    sealed interface HomeUiState {

        data object Loading : HomeUiState

        data class Loaded(
            val eventsHome: List<HomeEvent>,
            val isDetailsOpen: Boolean,
            val selectedEvent: EventOnDetailsUiState
        ) : HomeUiState

        companion object {
            fun createHomeDefault() = Loaded(
                eventsHome = listOf(
                    HomeEvent(
                        id = "dsfsdfsdf",
                        name = "Мероприятие 1",
                        isSelected = true,
                        imagePath = ""
                    ),
                    HomeEvent(
                        id = "dsfsdfsdfsafasdf",
                        name = "Мероприятие 2",
                        isSelected = false,
                        imagePath = ""
                    ),
                ),
                isDetailsOpen = false,
                selectedEvent = EventOnDetailsUiState.NoEventSelected(false, 0)
            )
        }
    }

    sealed interface EventOnDetailsUiState {
        val isLoading: Boolean
        val selectedTabOnDetails: Int

        data class NoEventSelected(
            override val isLoading: Boolean,
            override val selectedTabOnDetails: Int

        ) : EventOnDetailsUiState

        data class EventSelected(
            override val isLoading: Boolean,
            override val selectedTabOnDetails: Int,
            val eventId: String,
            val eventName: String,
            val users: List<LeaderUser>
        ) : EventOnDetailsUiState
    }

    data class HomeEvent(
        val id: String,
        val name: String,
        val isSelected: Boolean,
        val imagePath: String?
    )

    sealed interface DetailsUsersUiState {
        val isLoading: Boolean
        val selectedEventId: String

        data class NoFile(
            override val isLoading: Boolean,
            override val selectedEventId: String,

            ) : DetailsUsersUiState

        data class HasFile(
            override val isLoading: Boolean,
            override val selectedEventId: String,
            val users: List<LeaderUser>,
        ) : DetailsUsersUiState
    }
}

enum class DetailsSubScreens(val route: String) {
    Users("Участники"), Merge("Excel"), Data("Данные")
}

