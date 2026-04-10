package org.my.drivexcel.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import org.my.drivexcel.platform.MyWindowSizeClass
import org.my.drivexcel.ui.utils.nav.WindowSizeClassProvider


/**
 * Темная и светлая тема приложения
 */
@Composable
fun DriveXcelAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    myWindowSizeClass: MyWindowSizeClass,
    windowSizeClassProvider: WindowSizeClassProvider,
    content: @Composable () -> Unit
) {

    val windowSize = myWindowSizeClass.rememberWindowSizeClass()
    val windowSizeClass = windowSizeClassProvider.calculateWindowSizeClass()

    val colorScheme = when {
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    val backgroundColor = colorScheme.surface

    CompositionLocalProvider(
        LocalBackgroundColor provides backgroundColor,
        LocalWindowSize provides windowSize,
        LocalWindowSizeClass provides windowSizeClass
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = {
                DriveXcelBackground(content = content)
            }
        )
    }

}

/**
 * Обертка для цвета заднего фона
 */
@Composable
private fun DriveXcelBackground(
    content: @Composable () -> Unit
) {
    val color = LocalBackgroundColor.current

    Surface(
        color = if (color == Color.Unspecified) Color.Transparent else color
    ) {
        content()
    }
}
