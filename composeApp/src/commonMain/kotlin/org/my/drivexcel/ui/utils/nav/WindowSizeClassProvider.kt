package org.my.drivexcel.ui.utils.nav

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

interface WindowSizeClassProvider {

    @Composable
    fun calculateWindowSizeClass(): WindowSizeClass
}

class WindowSizeClassPreviewProvider(
    private val widthDp: Dp = 360.dp,
    private val heightDp: Dp = 640.dp
) : WindowSizeClassProvider {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    override fun calculateWindowSizeClass(): WindowSizeClass {
        return WindowSizeClass.calculateFromSize(
            DpSize(widthDp, heightDp)
        )
    }
}

fun WindowWidthSizeClass.maxContentWidth(
    compact: Dp = Dp.Unspecified,
    medium: Dp = 600.dp,
    expanded: Dp = 800.dp
): Dp {
    return when (this) {
        WindowWidthSizeClass.Compact -> compact
        WindowWidthSizeClass.Medium -> medium
        WindowWidthSizeClass.Expanded -> expanded
        else -> Dp.Unspecified
    }
}

val phoneWindowSizeClassPreviewProvider = WindowSizeClassPreviewProvider()
val desktopWindowSizeClassPreviewProvider = WindowSizeClassPreviewProvider(1280.dp, 800.dp)