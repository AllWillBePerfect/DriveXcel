package org.my.drivexcel.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.my.drivexcel.ui.theme.LocalWindowSizeClass
import org.my.drivexcel.ui.utils.nav.maxContentWidth


@Composable
fun CenteredContainerComponent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val maxWidth = LocalWindowSizeClass.current
        .widthSizeClass
        .maxContentWidth()

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .then(
                    if (maxWidth != Dp.Unspecified) {
                        Modifier.widthIn(max = maxWidth)
                    } else {
                        Modifier.fillMaxWidth()
                    }
                )
                .fillMaxWidth()
        ) {
            content()
        }
    }


}