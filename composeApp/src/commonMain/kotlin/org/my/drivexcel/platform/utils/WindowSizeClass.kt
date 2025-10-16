package org.my.drivexcel.platform.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

interface WindowSizeClass {

    @Composable
    fun rememberWindowSizeClass(): WindowSize
}

enum class WindowSize {
    Compact, Medium, Expanded

}

val WindowSize.isCompact get() = this == WindowSize.Compact

class WindowSizeClassPreview(
    private val widthDp: Dp = 360.dp,
    private val heightDp: Dp = 640.dp
) : WindowSizeClass {

    @Composable
    override fun rememberWindowSizeClass(): WindowSize {
        val windowDpSize = DpSize(widthDp, heightDp)

        return when {
            windowDpSize.width < 600.dp -> WindowSize.Compact
            windowDpSize.width < 840.dp -> WindowSize.Medium
            else -> WindowSize.Expanded
        }
    }
}