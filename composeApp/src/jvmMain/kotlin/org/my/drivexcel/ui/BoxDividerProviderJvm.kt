package org.my.drivexcel.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import org.jetbrains.skiko.Cursor
import org.my.drivexcel.ui.platform.BoxDividerProvider

class BoxDividerProviderJvm : BoxDividerProvider {
    @Composable
    override fun BoxDivider(
        dividerWidth: Dp,
        setOnDoubleTap: (Float) -> Unit,
        setOnDrag: (Float) -> Unit
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()

        val targetColor = if (isHovered)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        else
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)


        val animatedColor by animateColorAsState(
            targetValue = targetColor,
            animationSpec = tween(
                durationMillis = 250,
                delayMillis = 0
            ),
            label = "dividerColorAnim"
        )

        Box(
            modifier = Modifier
                .width(dividerWidth)
//                .clip(RoundedCornerShape(30.dp))
                .fillMaxHeight()
                .background(animatedColor)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { setOnDoubleTap(0.35f) }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        setOnDrag(dragAmount.x)
                    }
                }
                .pointerHoverIcon(
                    PointerIcon(org.jetbrains.skiko.Cursor(Cursor.E_RESIZE_CURSOR))
                )
                .hoverable(interactionSource = interactionSource)
        )
    }
}
