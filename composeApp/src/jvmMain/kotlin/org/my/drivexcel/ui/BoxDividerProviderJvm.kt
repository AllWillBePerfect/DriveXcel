package org.my.drivexcel.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.Colors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import org.jetbrains.skiko.Cursor
import org.my.drivexcel.ui.screens.home.platform.BoxDividerProvider

class BoxDividerProviderJvm : BoxDividerProvider {
    @Composable
    override fun BoxDivider(
        dividerWidth: Dp,
        setOnDoubleTap: (Float) -> Unit,
        setOnDrag: (Float) -> Unit
    ) {
        // Разделитель (ползунок)
        Box(
            modifier = Modifier
                .width(dividerWidth)
                .fillMaxHeight()
//                .background(MaterialTheme.colorScheme.outlineVariant)
                .background(Color.Transparent)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
//                            dividerPosition = 0.35f
                            setOnDoubleTap(0.35f)
                        }

                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        setOnDrag(dragAmount.x)
                    }

                }.pointerHoverIcon(
                    PointerIcon(
                        _root_ide_package_.org.jetbrains.skiko.Cursor(
                            Cursor.E_RESIZE_CURSOR
                        )
                    )
                )
        )
    }
}