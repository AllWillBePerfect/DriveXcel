package org.my.drivexcel.theme

import androidx.compose.runtime.staticCompositionLocalOf
import org.my.drivexcel.platform.utils.WindowSize

val LocalWindowSize = staticCompositionLocalOf<WindowSize> {
    error("No WindowSize provided")
}