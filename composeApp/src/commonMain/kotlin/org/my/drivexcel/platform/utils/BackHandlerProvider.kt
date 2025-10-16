package org.my.drivexcel.platform.utils

import androidx.compose.runtime.Composable

interface BackHandlerProvider {

    @Composable
    fun BackHandler(
        enabled: Boolean = true,
        onBack: () -> Unit
    )
}