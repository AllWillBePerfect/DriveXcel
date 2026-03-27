package org.my.drivexcel.v4.ui.screens.users

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.my.drivexcel.platform.utils.AppLogger
import org.my.drivexcel.v4.base.domain.ext.onFailure
import org.my.drivexcel.v4.base.domain.ext.onSuccess
import org.my.drivexcel.v4.domain.LeaderUserSearch
import org.my.drivexcel.v4.domain.model.LeaderUserDomainModel
import org.my.drivexcel.v4.domain.repository.GetLeaderUsersUseCase
import org.my.drivexcel.v4.domain.usecase.GetEventUseCase


class UsersViewModel(
    savedStateHandle: SavedStateHandle,
    private val eventId: String,
    private val logger: AppLogger,
    private val getEventUseCase: GetEventUseCase,
    private val getLeaderUsersUseCase: GetLeaderUsersUseCase
) : ViewModel() {

    private val leaderUserSearch = LeaderUserSearch()

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState = _uiState.asStateFlow()

    private val _queryFlow = MutableStateFlow("")
    private val _allUsers = MutableStateFlow<List<LeaderUserDomainModel>>(emptyList())


    init {

        logger.i("UsersViewModel", "init")


        viewModelScope.launch {
            launch {
                getEventUseCase(eventId)
                    .onSuccess {
                        _uiState.update { state ->
                            state.copy(
                                eventName = it.name
                            )
                        }
                    }
                    .onFailure { }
            }

            launch {
                /*val users = withContext(Dispatchers.Default) {
                    LeaderUserDomainModel.generateUsers(100)
                }
                _allUsers.value = users
                _uiState.update { state -> state.copy(
                    initialLoading = false,
                    isEmptyList = users.isEmpty()
                ) }*/


                getLeaderUsersUseCase(eventId).collect { users ->
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

            launch {
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
    }

    fun onAction(action: UsersUiAction) {
        when (action) {

            is UsersUiAction.OnTextTyped -> {
                _uiState.value = _uiState.value.copy(userInput = action.userText)
                _queryFlow.value = action.userText
            }
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
}

sealed interface UsersUiEvent {
    object NavigateBack : UsersUiEvent
}