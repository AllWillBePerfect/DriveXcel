package org.my.drivexcel.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

interface MyWindowSizeClass {

    @Composable
    fun rememberWindowSizeClass(): WindowSize
}

enum class WindowSize {
    Compact, Medium, Expanded

}

val WindowSize.isCompact get() = this == WindowSize.Compact
val WindowSize.isWide get() = this != WindowSize.Compact

class MyWindowSizeClassPreview(
    private val widthDp: Dp = 360.dp,
    private val heightDp: Dp = 640.dp
) : MyWindowSizeClass {

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

val phoneWindowSizeClassPreview = MyWindowSizeClassPreview()
val desktopWindowSizeClassPreview = MyWindowSizeClassPreview(widthDp = 1280.dp, heightDp = 800.dp)

