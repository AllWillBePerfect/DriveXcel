package org.my.drivexcel.ui.screens.addevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.my.drivexcel.data.EventData
import org.my.drivexcel.data.EventDirErrors
import org.my.drivexcel.data.EventDirectoryRepository
import org.my.drivexcel.data.fold
import org.my.drivexcel.domain.usecases.CreateEventUseCaseOld
import org.my.drivexcel.platform.utils.ImageConverter
import org.my.drivexcel.utils.ActionsManager

class AddEventViewModel(
    private val imageConverter: ImageConverter,
    private val eventDirectoryRepository: EventDirectoryRepository,
    private val actionsManager: ActionsManager,
    private val createEventUseCaseOld: CreateEventUseCaseOld
) : ViewModel() {

    private var pendingEditEventId: String? = null

    private val _eventName = MutableStateFlow("")
    val eventName = _eventName.asStateFlow()

    private val _image = MutableStateFlow<ByteArray?>(null)
    val image = _image.map { it?.let { imageConverter.byteArrayToImageBitmap(it) } }.flowOn(
        Dispatchers.IO
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val isButtonEnabled = combine(
        _eventName,
        _isLoading,
    ) { name, loading ->
        name.isNotEmpty() && !loading
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    fun changeEventName(eventName: String) {
        _eventName.value = eventName
    }

    fun setImage(byteArray: ByteArray) {
        _image.value = byteArray
    }

    fun removeImage() {
        _image.value = null
    }

    fun createEventDirectory() {
        viewModelScope.launch {
            _isLoading.update { true }
            /*val res = eventDirectoryRepository.createEventDir(
                eventData = EventData(
                    name = _eventName.value,
                    image = _image.value
                )
            )
            res.fold(
                onSuccess = {
                    actionsManager.eventCreated()
                },
                onError = { e ->
                    actionsManager.eventFailed(e.parseToString())
                }
            )*/
            val res2 = createEventUseCaseOld.invoke(_eventName.value, _image.value)
            res2.fold(
                onSuccess = {

                },
                onError = {e ->
                    actionsManager.eventFailed(e.parseToString())
                }
            )
            _isLoading.update { false }
        }
    }

    fun redactingEventDirectory(id: String) {
        viewModelScope.launch {
            _isLoading.update { true }
            val res = eventDirectoryRepository.changeEventDir(
                id = id,
                eventData = EventData(
                    name = _eventName.value,
                    image = _image.value
                )
            )
            res.fold(
                onSuccess = {
                    actionsManager.eventChanged()
                },
                onError = { e ->
                    actionsManager.eventFailed(e.parseToString())
                }
            )

            _isLoading.update { false }
        }
    }

    suspend fun loadSavedEventData(id: String) {
        _isLoading.update { true }

//        val eventDirectory = eventDirectoryRepository.getEvent(id)
        val eventDirectory = eventDirectoryRepository.getEventFlow(id).first()
        println("eventDirectory: $eventDirectory")
        eventDirectory?.let { data ->
            setSavedEventData(
                EventData(
                    name = data.name,
                    image = data.image?.readBytes()
//                    image = data.imagePath?.let { path -> File(path).readBytes() }
                )
            )
        } ?: actionsManager.eventFailed("Не получилось загрузить данные")
        _isLoading.update { false }

    }

    fun getPendingId(): String? = pendingEditEventId

    fun setPendingId(id: String) {
        pendingEditEventId = id
    }

    fun cleanPendingEvent() {
        pendingEditEventId = null
        _eventName.value = ""
        _image.value = null
    }

    private fun setSavedEventData(eventData: EventData) {
        _eventName.value = eventData.name
        _image.value = eventData.image
    }

    private fun EventDirErrors.parseToString() = when (this) {
        EventDirErrors.AlreadyExists -> "Директория уже существует"
        EventDirErrors.CorruptedMeta -> "Не получилось прочитать meta файл"
        EventDirErrors.InvalidName -> "Недопустимое имя"
        EventDirErrors.NotFoundDir -> "Директория не найдена"
        is EventDirErrors.Unhandled -> "Ошибка: ${this.exception}"
    }

}