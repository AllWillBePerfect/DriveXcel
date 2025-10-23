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
import org.my.drivexcel.platform.utils.WindowSizeClass
import org.my.drivexcel.theme.DriveXcelAppTheme
import java.awt.Dimension


fun main() {
    initKoinWithModules()
    application {

        Window(
            onCloseRequest = ::exitApplication,
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


            val windowSizeClass: WindowSizeClass = koinInject()

            DriveXcelAppTheme(
                darkTheme = themeSettings.isDarkMode,
                windowSizeClass = windowSizeClass
            ) {
                DriveXcelApp(
                )
            }
        }
    }
}


private data class ThemeSettings(
    val isDarkMode: Boolean
)


/*
fun isSystemInDarkThemeFlow() = callbackFlow {
    val currentDark = isSystemDark()
    trySend(currentDark)

    val listener = PropertyChangeListener {
        trySend(isSystemDark())
    }

    Toolkit.getDefaultToolkit().addPropertyChangeListener("win.lightTheme", listener)
    Toolkit.getDefaultToolkit().addPropertyChangeListener("mac.themeChanged", listener)

    awaitClose {
        Toolkit.getDefaultToolkit().removePropertyChangeListener(listener)
    }
}
    .distinctUntilChanged()
    .conflate()

private fun isSystemDark(): Boolean {
    val osName = System.getProperty("os.name").lowercase()
    return when {
        osName.contains("mac") -> {
            val theme = Runtime.getRuntime().exec(arrayOf("defaults", "read", "-g", "AppleInterfaceStyle"))
                .inputStream.bufferedReader().readText().trim()
            theme.equals("dark", ignoreCase = true)
        }
        osName.contains("win") -> {
            val process = Runtime.getRuntime().exec(
                arrayOf("reg", "query",
                    "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                    "/v", "AppsUseLightTheme")
            )
            val output = process.inputStream.bufferedReader().readText()
            "0x0" in output // 0x0 = тёмная тема
        }
        else -> false // на Linux определить сложно — лучше просто возвращать false или настраивать вручную
    }
}
*/

