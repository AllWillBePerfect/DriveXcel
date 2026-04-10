package org.my.drivexcel.ui.theme

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.staticCompositionLocalOf
import org.my.drivexcel.platform.WindowSize

val LocalWindowSize = staticCompositionLocalOf<WindowSize> {
    error("No MyWindowSize provided")
}

val LocalWindowSizeClass =
    staticCompositionLocalOf<WindowSizeClass> { error("No WindowSizeClass provided") }