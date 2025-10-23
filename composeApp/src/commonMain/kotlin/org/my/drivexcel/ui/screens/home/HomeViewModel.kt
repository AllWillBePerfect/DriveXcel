package org.my.drivexcel.ui.screens.home

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.my.drivexcel.data.EventsDataSource
import org.my.drivexcel.platform.datasources.DirectoriesDataSource
import org.my.drivexcel.platform.utils.ImageConverter
import org.my.drivexcel.platform.utils.LeaderUser
import org.my.drivexcel.platform.utils.XlsReader

class HomeViewModel(
    private val eventsDataSource: EventsDataSource,
    private val directoriesDataSource: DirectoriesDataSource,
    private val xlsReader: XlsReader,
    private val imageConverter: ImageConverter
) : ViewModel() {


    private val viewModelState = MutableStateFlow(
        HomeViewModelState()
    )

    val eventDirectories = directoriesDataSource.events

    // 🔸 Поток, который реагирует на смену selectedEventId
    @OptIn(ExperimentalCoroutinesApi::class)
    private val selectedUsersFlow = viewModelState
        .map { it.selectedEventId }
        .distinctUntilChanged()
        .flatMapLatest { selectedId ->
            if (selectedId == null) {
                flowOf(emptyList())
            } else {
                flow {
                    val path = directoriesDataSource.findExcelFilePathByEventName(selectedId)
                    val users = path?.let { xlsReader.readExcel(it) } ?: emptyList()
                    emit(users)
                }
            }
        }

    val uiState: StateFlow<HomeUiState> = combine(
        viewModelState,
        eventDirectories,
        selectedUsersFlow
    ) { state, events, users ->
        val homeEvents = events.map {
            HomeEvent(
                id = it.eventName,
                isSelected = it.eventName == state.selectedEventId,
                image = it.image?.let { byteArray -> imageConverter.byteArrayToImageBitmap(byteArray) }
            )
        }
//        delay(5000)
//        val selectedEvent = homeEvents.find { it.id == state.selectedEventId }
        state.copy(
            eventsList = homeEvents
        ).toUiState(users)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeViewModelState().toUiState(emptyList())
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


    data class HomeViewModelState(
        val eventsList: List<HomeEvent>? = null,
        val isDetailsOpen: Boolean = false,
        val selectedEventId: String? = null
    ) {

        fun toUiState(users: List<LeaderUser>): HomeUiState {

            if (eventsList == null) {
                return HomeUiState.Loading
            } else {
                val selected = eventsList.find { it.id == selectedEventId }
                val detailsOpen = selected != null && isDetailsOpen

                return HomeUiState.Loaded(
                    eventsHome = eventsList,
                    isDetailsOpen = detailsOpen,
                    selectedEvent = selected
                        ?.let { homeEvent ->
                            EventOnDetailsUiState.EventSelected(
                                eventId = homeEvent.id,
                                users = users
                            )
                        }
                        ?: EventOnDetailsUiState.NoEventSelected
                )
            }


        }
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
                eventsHome = emptyList(),
                isDetailsOpen = false,
                selectedEvent = EventOnDetailsUiState.NoEventSelected
            )
        }
    }

    sealed interface EventOnDetailsUiState {
        data object NoEventSelected : EventOnDetailsUiState
        data class EventSelected(
            val eventId: String,
            val users: List<LeaderUser>
        ) : EventOnDetailsUiState
    }

    data class HomeEvent(
        val id: String,
        val isSelected: Boolean,
        val image: ImageBitmap?
    )
}

