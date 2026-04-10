package org.my.drivexcel.ui.screens.users

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.my.drivexcel.platform.AppLogger
import org.my.drivexcel.base.domain.wrapper.FlowResult
import org.my.drivexcel.domain.LeaderUserSearch
import org.my.drivexcel.domain.model.LeaderUserDomainModel
import org.my.drivexcel.domain.usecase.ObserveLeaderUsersUseCase
import org.my.drivexcel.domain.usecase.ObserveEventUseCase
import org.my.drivexcel.domain.usecase.GetEventUseCase
import org.my.drivexcel.ui.platform.ClipboardManager


class UsersViewModel(
    savedStateHandle: SavedStateHandle,
    private val logger: AppLogger,
    private val clipboardManager: ClipboardManager,
    private val getEventUseCase: GetEventUseCase,
    private val observeEventUseCase: ObserveEventUseCase,
    private val observeLeaderUsersUseCase: ObserveLeaderUsersUseCase
) : ViewModel() {

    private val leaderUserSearch = LeaderUserSearch()

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState = _uiState.asStateFlow()

    private val _queryFlow = MutableStateFlow("")
    private val _allUsers = MutableStateFlow<List<LeaderUserDomainModel>>(emptyList())

    private var currentEventId: String? = null
    private var eventJob: Job? = null

    fun load(eventId: String) {
        if (currentEventId == eventId) return
        logger.i("UsersViewModel", "ID: $eventId")

        currentEventId = eventId
        logger.i("UsersViewModel", "load: $eventId")

        viewModelScope.launch {

            eventJob?.cancel()

            eventJob = viewModelScope.launch {
                observeEventUseCase(eventId).collect { event ->
                    when (event) {
                        is FlowResult.Success -> {
                            _uiState.update { state ->
                                state.copy(eventName = event.data.name)
                            }
                        }

                        is FlowResult.Error -> {}
                        FlowResult.Loading -> {}

                    }
                }
            }


            /*launch {
                getEventUseCase(eventId)
                    .onSuccess {
                        _uiState.update { state ->
                            state.copy(eventName = it.name)
                        }
                    }
                    .onFailure { }
            }*/


            launch {
                observeLeaderUsersUseCase(eventId).collect { users ->
                    logger.i("UsersViewModel", "users: ${users.count()}")
                    _allUsers.value = users

                    _uiState.update { state ->
                        state.copy(
                            initialLoading = false,
                            isEmptyList = users.isEmpty()
                        )
                    }
                }
            }
        }
    }

    fun exit() {
        currentEventId = null
    }

    init {

        logger.i("UsersViewModel", "init")


        viewModelScope.launch {


            combine(
                _queryFlow.debounce(300),
                _allUsers
            ) { query, users ->
                if (query.isBlank()) {
                    users
                } else {
                    withContext(Dispatchers.Default) {
                        leaderUserSearch.search(query, users)
                    }
                }
            }.collect { filteredUsers ->
                logger.i("UsersViewModel", "search: ${filteredUsers.size}")
                _uiState.update {
                    it.copy(
                        users = filteredUsers
                    )
                }
            }

        }
    }

    fun onAction(action: UsersUiAction) {
        when (action) {

            is UsersUiAction.OnTextTyped -> {
                _uiState.value = _uiState.value.copy(userInput = action.userText)
                _queryFlow.value = action.userText
            }

            is UsersUiAction.CopyUserId -> clipboardManager.copy(action.userId)
        }
    }

}

data class UsersUiState(
    val id: String = "",
    val eventName: String = "",
    val users: List<LeaderUserDomainModel> = emptyList(),
    val userInput: String = "",
    val initialLoading: Boolean = true,
    val isEmptyList: Boolean = false
)

sealed interface UsersUiAction {
    data class OnTextTyped(val userText: String) : UsersUiAction
    data class CopyUserId(val userId: String): UsersUiAction
}

sealed interface UsersUiEvent {
    object NavigateBack : UsersUiEvent
}