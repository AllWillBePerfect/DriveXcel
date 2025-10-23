package org.my.drivexcel.ui.screens.addevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.my.drivexcel.data.EventData
import org.my.drivexcel.data.EventDirectoryManager
import org.my.drivexcel.data.EventOpResult
import org.my.drivexcel.platform.utils.ImageConverter
import org.my.drivexcel.utils.ActionsManager
import java.io.File

class AddEventViewModel(
    private val imageConverter: ImageConverter,
    private val eventDirectoryManager: EventDirectoryManager,
    private val actionsManager: ActionsManager
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
            val res = eventDirectoryManager.createEventDir(
                newData = EventData(
                    name = _eventName.value,
                    image = _image.value
                )
            )
            if (res is EventOpResult.Success) {
                actionsManager.eventCreated()
            } else {
                actionsManager.eventFailed(res::class.simpleName.toString())
            }
            _isLoading.update { false }
        }
    }

    fun redactingEventDirectory(id: String) {
        viewModelScope.launch {
            _isLoading.update { true }
            val res = eventDirectoryManager.redactingEventDir(
                id = id,
                newData = EventData(
                    name = _eventName.value,
                    image = _image.value
                )
            )
            if (res is EventOpResult.Success) {
                actionsManager.eventChanged()
            } else {
                actionsManager.eventFailed(res::class.simpleName.toString())
            }
            _isLoading.update { false }
        }
    }

    suspend fun loadSavedEventData(id: String) {
        _isLoading.update { true }

        val eventDirectory = eventDirectoryManager.getEvent(id)
        eventDirectory?.let { data ->
            setSavedEventData(
                EventData(
                    name = data.name,
                    image = data.imagePath?.let { path -> File(path).readBytes() }
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

}