package org.my.drivexcel.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

interface BoxDividerProvider {
    @Composable
    fun BoxDivider(
        dividerWidth: Dp,
        setOnDoubleTap: (Float) -> Unit,
        setOnDrag: (Float) -> Unit,
    )
}