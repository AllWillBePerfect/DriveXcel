package org.my.drivexcel.ui.screens.addevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.my.drivexcel.platform.datasources.DirectoriesDataSource
import org.my.drivexcel.platform.utils.ImageConverter

class AddEventViewModel(
    private val imageConverter: ImageConverter,
    private val directoriesDataSource: DirectoriesDataSource
) : ViewModel() {

    private val _eventName = MutableStateFlow("")
    val eventName = _eventName.asStateFlow()

    private val _image = MutableStateFlow<ByteArray?>(null)
    val image = _image.map { it?.let { imageConverter.byteArrayToImageBitmap(it) } }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val isButtonEnabled = _eventName
        .map { it.isNotEmpty() }
        .stateIn(
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
        directoriesDataSource.createEventDirectory(
            eventName = _eventName.value,
            imageBytes = _image.value
        )
    }

}