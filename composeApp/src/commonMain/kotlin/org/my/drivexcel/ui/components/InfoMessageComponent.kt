package org.my.drivexcel.v4.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_error
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.my.drivexcel.platform.phoneWindowSizeClassPreview
import org.my.drivexcel.ui.theme.DriveXcelAppTheme
import org.my.drivexcel.ui.models.UiIcon
import org.my.drivexcel.ui.models.UiText
import org.my.drivexcel.ui.utils.nav.windowSizeClassPreviewProvider

@Composable
fun InfoMessageComponent(
    modifier: Modifier = Modifier,
    icon: UiIcon,
    text: UiText
) {

    val iconSize = remember { 48.dp }
    val shape = remember { RoundedCornerShape(8.dp) }


    Column(
        modifier = modifier
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = shape
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.surfaceContainerHighest,
                shape
            )
            .padding(16.dp)

        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (icon) {
            is UiIcon.Vector -> Icon(
                imageVector = icon.imageVector,
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )

            is UiIcon.Drawable -> Icon(
                painter = painterResource(icon.res),
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
        }
        when (text) {
            is UiText.AnnotatedString -> {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    text = text.annotatedString,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
            is UiText.Text -> {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    text = text.text,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewNight() = DriveXcelAppTheme(
    darkTheme = true,
    myWindowSizeClass = phoneWindowSizeClassPreview,
    windowSizeClassProvider = windowSizeClassPreviewProvider

) {
    InfoMessageComponent(
        icon = UiIcon.Drawable(Res.drawable.ic_error),
        text = UiText.Text("Произошла ошибка при загрузке мероприятия.")
    )
}

@Preview
@Composable
private fun PreviewLight() = DriveXcelAppTheme(
    darkTheme = false,
    myWindowSizeClass = phoneWindowSizeClassPreview,
    windowSizeClassProvider = windowSizeClassPreviewProvider


) {
    InfoMessageComponent(
        icon = UiIcon.Drawable(Res.drawable.ic_error),
        text = UiText.Text("Произошла ошибка при загрузке мероприятия.")
    )
}

