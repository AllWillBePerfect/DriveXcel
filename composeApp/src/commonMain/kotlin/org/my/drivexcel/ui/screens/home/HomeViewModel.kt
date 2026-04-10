package org.my.drivexcel.ui.screens.home

import androidx.lifecycle.ViewModel
import org.my.drivexcel.ui.utils.SnackbarManager

class HomeViewModel(
    private val snackbarManager: SnackbarManager
) : ViewModel() {

    val messages = snackbarManager.messages
}