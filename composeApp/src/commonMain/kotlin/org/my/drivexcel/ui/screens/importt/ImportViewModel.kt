package org.my.drivexcel.ui.screens.importt

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.my.drivexcel.platform.AppLogger
import org.my.drivexcel.base.domain.ext.onFailure
import org.my.drivexcel.base.domain.ext.onSuccess
import org.my.drivexcel.domain.usecase.SaveLeaderUsersUseCase
import org.my.drivexcel.ui.models.PickedFile
import org.my.drivexcel.ui.screens.importt.models.ImportUiAction
import org.my.drivexcel.ui.screens.importt.models.ImportUiState
import org.my.drivexcel.ui.screens.importt.models.XlsStateUiItem
import org.my.drivexcel.ui.utils.SnackbarAction
import org.my.drivexcel.ui.utils.SnackbarManager
import org.my.drivexcel.ui.utils.sha256

class ImportViewModel(
    savedStateHandle: SavedStateHandle,
    private val logger: AppLogger,
    private val snackbarManager: SnackbarManager,
    private val saveLeaderUsersUseCase: SaveLeaderUsersUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportUiState())
    val uiState = _uiState.asStateFlow()

    private var currentEventId: String? = null

    fun load(eventId: String) {
        if (currentEventId == eventId) return

        _uiState.value = ImportUiState()
        currentEventId = eventId
        logger.i("ImportViewModel", "load: $eventId")

    }

    fun onAction(action: ImportUiAction) {
        when (action) {
            is ImportUiAction.OnDeleteClicked -> {
                _uiState.update {
                    it.copy(
                        files = _uiState.value.files.filterNot { file -> file.id == action.id }
                    )
                }
            }

            ImportUiAction.OnSaveXlsClicked -> {

                val eventId = currentEventId ?: return // 🔥 защита

                _uiState.update { it.copy(isSaving = true) }
                viewModelScope.launch {
                    saveLeaderUsersUseCase(
                        id = eventId,
                        xlsList = uiState.value.files.map { it.bytes }
                    )
                        .onSuccess {
                            _uiState.update { it.copy(files = emptyList()) }
                            snackbarManager.send(SnackbarAction.LeaderUsersSaved)
                        }
                        .onFailure {
                            snackbarManager.send(SnackbarAction.ExceptionAppear(it))
                            logger.e("ImportViewModel", it.cause.toString())
                        }
                    _uiState.update { it.copy(isSaving = false) }
                }
            }
        }
    }

    fun onFilesSelected(files: List<PickedFile>) {
        logger.i("ImportViewModel", "files: ${files.size}")
        _uiState.update { state ->

            val existingIds = state.files.map { it.id }.toSet()

            val newItems = files.mapNotNull { file ->
                val id = file.bytes.sha256()
                if (id in existingIds) null
                else XlsStateUiItem(id = id, fileName = file.name, bytes = file.bytes)
            }

            state.copy(files = state.files + newItems)

        }
    }
}



