package org.my.drivexcel.v4.ui.screens.home

import androidx.lifecycle.ViewModel
import org.my.drivexcel.v4.ui.utils.SnackbarManager

class HomeViewModel(
    private val snackbarManager: SnackbarManager
) : ViewModel() {

    val messages = snackbarManager.messages
}