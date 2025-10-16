package org.my.drivexcel.platform.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

class WindowSizeClassJvm : WindowSizeClass {
    @Composable
    override fun rememberWindowSizeClass(): WindowSize {
        val windowInfo = LocalWindowInfo.current
        val density = LocalDensity.current

        val sizePx = windowInfo.containerSize
        val windowDpSize = with(density) {
            DpSize(sizePx.width.toDp(), sizePx.height.toDp())
        }

        return when {
            windowDpSize.width < 600.dp -> WindowSize.Compact
            windowDpSize.width < 840.dp -> WindowSize.Medium
            else -> WindowSize.Expanded
        }
    }
}