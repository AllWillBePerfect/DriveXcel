package org.my.drivexcel.v4.ui.screens.importt.models

sealed interface ImportUiAction {
    data class OnDeleteClicked(val id: String) : ImportUiAction
    object OnSaveXlsClicked : ImportUiAction
}