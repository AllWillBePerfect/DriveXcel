package org.my.drivexcel.v4.ui.screens.event_editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.my.drivexcel.data.onFailure
import org.my.drivexcel.data.onSuccess
import org.my.drivexcel.domain.models.EventImageMapperV2
import org.my.drivexcel.platform.utils.AppLogger
import org.my.drivexcel.v4.base.domain.exception.DomainException
import org.my.drivexcel.v4.base.domain.ext.onFailure
import org.my.drivexcel.v4.base.domain.ext.onSuccess
import org.my.drivexcel.v4.domain.exception.EventEditingDomainException
import org.my.drivexcel.v4.domain.usecase.CreateEventUseCase
import org.my.drivexcel.v4.domain.usecase.DeleteEventUseCase
import org.my.drivexcel.v4.domain.usecase.GetEventUseCase
import org.my.drivexcel.v4.domain.usecase.UpdateEventUseCase
import org.my.drivexcel.v4.ui.utils.SnackbarAction
import org.my.drivexcel.v4.ui.utils.SnackbarManager


class EventEditorViewModel(
    savedStateHandle: SavedStateHandle,
    private val logger: AppLogger,
    private val snackbarManager: SnackbarManager,
    private val createEventUseCase: CreateEventUseCase,
    private val getEventUseCase: GetEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val eventImageMapperV2: EventImageMapperV2
) : ViewModel() {

    private val eventId: String? = savedStateHandle.get<String>("eventId")
    private val mode = eventId.toEditorMode()

    private val initialState = EventEditorUiState(
        userInput = "",
        eventEditorMode = mode,
        isInitialLoading = mode == EventEditorMode.UPDATE
    )

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EventEditorEvent>()
    val events = _events.asSharedFlow()

    init {
        if (mode == EventEditorMode.UPDATE) {
            eventId?.let {
                updateState { it.copy(isInitialLoading = true) }

                viewModelScope.launch {
                    getEventUseCase(it)
                        .onSuccess { model ->
                            updateState {
                                it.copy(
                                    userInput = model.name,
                                    userImage = model.byteArray?.let {
                                        logger.e("EventEditorViewModel", "image: $it")
                                        ImageBytes(it)
                                    }
                                )
                            }
                        }
                        .onFailure {
                            updateState { it.copy(isGetEventLoadError = true) }
                            logger.e("EventEditorViewModel", "fail to load event: $it")
                        }
                    updateState { it.copy(isInitialLoading = false) }
                }
            }
        }
    }

    fun onAction(action: EventEditingAction) {
        when (action) {
            EventEditingAction.BackClicked -> sendEvent(EventEditorEvent.NavigateBack)
            EventEditingAction.ImageClicked -> sendEvent(EventEditorEvent.LaunchImagePicker)

            is EventEditingAction.InputChanged ->
                updateState { it.copy(userInput = action.input, textFieldError = null) }

            is EventEditingAction.ImagePicked ->
                updateState { it.copy(userImage = action.byteArray) }

            EventEditingAction.ImageRemoved ->
                updateState { it.copy(userImage = null) }

            EventEditingAction.CreateEvent -> {
                _uiState.update { it.copy(isEditingInProcess = true) }
                val currentState = uiState.value

                if (mode == EventEditorMode.CREATE) {
                    viewModelScope.launch {
                        createEventUseCase(
                            eventName = currentState.userInput,
                            byteArray = currentState.userImage?.bytes
                        )
                            .onSuccess {
                                snackbarManager.send(SnackbarAction.EventCreated)
                                _events.emit(EventEditorEvent.NavigateBack)
                            }
                            .onFailure(::errorHandle)
                    }
                } else {
                    viewModelScope.launch {
                        eventId?.let {
                            updateEventUseCase(
                                id = it,
                                eventName = currentState.userInput,
                                byteArray = currentState.userImage?.bytes
                            ).onSuccess {
                                snackbarManager.send(SnackbarAction.EventUpdated)
                                _events.emit(EventEditorEvent.NavigateBack)
                            }.onFailure(::errorHandle)
                        }
                    }
                }
            }

            EventEditingAction.DeleteEvent -> {
                viewModelScope.launch {
                    eventId?.let {
                        deleteEventUseCase(it)
                            .onSuccess {
                                snackbarManager.send(SnackbarAction.EventDeleted)
                            }
                            .onFailure(::deleteErrorHandle)
                    }
                }
            }
        }
    }

    private inline fun updateState(
        reducer: (EventEditorUiState) -> EventEditorUiState
    ) {
        _uiState.update(reducer)
    }

    private fun sendEvent(event: EventEditorEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    private fun String?.toEditorMode(): EventEditorMode =
        if (this == null) EventEditorMode.CREATE else EventEditorMode.UPDATE


    private fun errorHandle(e: EventEditingDomainException) {
        logger.e("EventEditorViewModel", "Create/Update event failed", e)
        when (e) {
            is EventEditingDomainException.EmptyName -> {
                updateState { it.copy(textFieldError = "Имя не может быть пустым") }
            }

            is EventEditingDomainException.NameAlreadyExists -> {
                updateState { it.copy(textFieldError = "Такое имя уже занято") }
            }

            is EventEditingDomainException.Unknown -> eventEditingUnknownErrorHandle(e)
        }
        _uiState.update { it.copy(isEditingInProcess = false) }

    }


    private fun deleteErrorHandle(e: DomainException) {
        viewModelScope.launch {
            snackbarManager.send(SnackbarAction.ExceptionAppear(e))
        }
        logger.e(
            "EventEditorViewModel",
            "Произошла ошибка при удалении мероприятия: ${e.cause?.javaClass?.simpleName}"
        )
    }

    private fun eventEditingUnknownErrorHandle(e: EventEditingDomainException) {
        viewModelScope.launch {
            snackbarManager.send(SnackbarAction.ExceptionAppear(e))
        }
        logger.e(
            "EventEditorViewModel",
            "Произошла ошибка при создании/редактировании мероприятия: ${e.cause?.javaClass?.simpleName}"
        )
    }
}

data class EventEditorUiState(
    val userInput: String = "",
    val userImage: ImageBytes? = null,
    val isInitialLoading: Boolean = false,
    val isGetEventLoadError: Boolean = false,
    val isEditingInProcess: Boolean = false,
    val isSaving: Boolean = false,
    val eventEditorMode: EventEditorMode = EventEditorMode.CREATE,
    val textFieldError: String? = null
) {
    val isButtonEnable: Boolean
        get() = userInput.isNotBlank() && !isEditingInProcess

    val isRemoveImageButtonEnabled: Boolean
        get() = userImage != null
}

enum class EventEditorMode {
    CREATE, UPDATE
}

val EventEditorMode.title: String
    get() = when (this) {
        EventEditorMode.CREATE -> "Создать"
        EventEditorMode.UPDATE -> "Редактировать"
    }

sealed interface EventEditingAction {
    object BackClicked : EventEditingAction
    object ImageClicked : EventEditingAction
    data class InputChanged(
        val input: String
    ) : EventEditingAction

    data class ImagePicked(
        val byteArray: ImageBytes
    ) : EventEditingAction

    object ImageRemoved : EventEditingAction
    object CreateEvent : EventEditingAction
    object DeleteEvent : EventEditingAction
}

sealed interface EventEditorEvent {
    object NavigateBack : EventEditorEvent
    object LaunchImagePicker : EventEditorEvent

}

@JvmInline
value class ImageBytes(val bytes: ByteArray)