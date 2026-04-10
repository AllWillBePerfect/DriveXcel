package org.my.drivexcel.ui.utils.nav

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

fun customNavigationSuiteType(
    myWindowSizeClass: WindowSizeClass
): NavigationSuiteType {

    val width = myWindowSizeClass.widthSizeClass
    val height = myWindowSizeClass.heightSizeClass

    return when (width) {

        // Phones
        WindowWidthSizeClass.Compact -> {
            NavigationSuiteType.ShortNavigationBarCompact
        }

        // Large phones / small tablets
        WindowWidthSizeClass.Medium -> {
            when (height) {
                WindowHeightSizeClass.Compact ->
                    // Landscape / split screen
                    NavigationSuiteType.NavigationBar

                WindowHeightSizeClass.Medium,
                WindowHeightSizeClass.Expanded ->
                    NavigationSuiteType.ShortNavigationBarMedium

                else ->
                    NavigationSuiteType.ShortNavigationBarMedium
            }
        }

        // Tablets / Desktop
        WindowWidthSizeClass.Expanded -> {
            when (height) {
                WindowHeightSizeClass.Expanded -> {
                    // Desktop / ChromeOS
                    NavigationSuiteType.NavigationDrawer
                }

                WindowHeightSizeClass.Compact -> {
                    // Landscape tablet / short window
                    NavigationSuiteType.NavigationRail
                }

                WindowHeightSizeClass.Medium -> {
                    NavigationSuiteType.WideNavigationRailCollapsed
                }

                else -> NavigationSuiteType.WideNavigationRailCollapsed
            }
        }

        else -> NavigationSuiteType.ShortNavigationBarCompact
    }
}