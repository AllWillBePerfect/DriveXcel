package org.my.drivexcel.ui.screens.importt.models

data class ImportUiState(
    val files: List<XlsStateUiItem> = emptyList(),
    val isSaving: Boolean = false

) {
    val isSaveEnabled: Boolean
        get() = files.isNotEmpty() && !isSaving

    val isEmptyList: Boolean
        get() = files.isEmpty()
}