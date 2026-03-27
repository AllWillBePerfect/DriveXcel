package org.my.drivexcel.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import org.my.drivexcel.platform.utils.WindowSizeClass


/**
 * Темная и светлая тема приложения
 */
@Composable
fun DriveXcelAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    windowSizeClass: WindowSizeClass,
    content: @Composable () -> Unit
) {

    val windowSize = windowSizeClass.rememberWindowSizeClass()

    val colorScheme = when {
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    val backgroundColor = colorScheme.surface

    CompositionLocalProvider(
        LocalBackgroundColor provides backgroundColor,
        LocalWindowSize provides windowSize

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
