package org.my.drivexcel.v4.platform

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import org.my.drivexcel.ui.utils.nav.WindowSizeClassProvider

class WindowSizeClassProviderAndroid : WindowSizeClassProvider {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    override fun calculateWindowSizeClass(): WindowSizeClass {
        return androidx.compose.material3.windowsizeclass.calculateWindowSizeClass(LocalActivity.current!!)
    }
}