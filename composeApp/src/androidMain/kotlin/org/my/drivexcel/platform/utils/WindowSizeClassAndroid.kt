package org.my.drivexcel.platform.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetricsCalculator

class WindowSizeClassAndroid(
    private val context: Context
) : WindowSizeClass{

    @Composable
    override fun rememberWindowSizeClass(): WindowSize {
        val configuration = LocalConfiguration.current
        val windowMetrics = remember(configuration) {
            WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(context)
        }
        val windowDpSize = with(LocalDensity.current) {
            windowMetrics.bounds.toComposeRect().size.toDpSize()
        }
        return when {
            windowDpSize.width < 600.dp -> WindowSize.Compact
            windowDpSize.width < 840.dp -> WindowSize.Medium
            else -> WindowSize.Expanded
        }
    }
}