package org.my.drivexcel.ui.screens.importt.models

sealed interface ImportUiAction {
    data class OnDeleteClicked(val id: String) : ImportUiAction
    object OnSaveXlsClicked : ImportUiAction
}