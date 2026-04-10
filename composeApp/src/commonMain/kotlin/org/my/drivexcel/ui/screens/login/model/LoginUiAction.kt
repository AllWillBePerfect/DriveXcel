package org.my.drivexcel.ui.screens.login.model

sealed interface LoginUiAction {
    data object AuthorizeUser : LoginUiAction
}