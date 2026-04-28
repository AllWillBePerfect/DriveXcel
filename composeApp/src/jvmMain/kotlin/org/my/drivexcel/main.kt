package org.my.drivexcel

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.di.initKoinWithModules
import org.my.drivexcel.platform.MyWindowSizeClass
import org.my.drivexcel.ui.theme.DriveXcelAppTheme
import org.my.drivexcel.ui.navigation.App
import org.my.drivexcel.ui.utils.nav.WindowSizeClassProvider
import java.awt.Dimension


fun main() {


    initKoinWithModules()
    application {
        Window(
            onCloseRequest = {
                exitApplication()
            },
            title = "DriveXcel",
            state = rememberWindowState(
                size = DpSize(
                    width = 1280.dp,
                    height = 800.dp
                )
            ),
        ) {

            window.minimumSize = Dimension(400, 400)

            val themeViewModel: ThemeViewModel = koinViewModel()
            val systemDark = isSystemInDarkTheme()
            val uiState by themeViewModel.userSettings.collectAsState()

            val themeSettings = ThemeSettings(
                isDarkMode = uiState.shouldUseDarkTheme(systemDark)
            )


            val myWindowSizeClass: MyWindowSizeClass = koinInject()
            val windowSizeClassProvider: WindowSizeClassProvider = koinInject()

            DriveXcelAppTheme(
                darkTheme = themeSettings.isDarkMode,
                myWindowSizeClass = myWindowSizeClass,
                windowSizeClassProvider = windowSizeClassProvider
            ) {
//                DriveXcelApp(
//                )
                App()
            }
        }
    }


}

private data class ThemeSettings(
    val isDarkMode: Boolean
)







