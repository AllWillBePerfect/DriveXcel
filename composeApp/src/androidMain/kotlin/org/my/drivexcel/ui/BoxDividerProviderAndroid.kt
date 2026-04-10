package org.my.drivexcel.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import org.my.drivexcel.ui.platform.BoxDividerProvider

/**
 * Реализацию под android разделитель делать не нужно, поэтому оставим ее пустой
 */
class BoxDividerProviderAndroid : BoxDividerProvider {
    @Composable
    override fun BoxDivider(
        dividerWidth: Dp,
        setOnDoubleTap: (Float) -> Unit,
        setOnDrag: (Float) -> Unit
    ) {
        null
    }
}