package org.my.drivexcel.ui.screens.events.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.preview
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.my.drivexcel.platform.phoneWindowSizeClassPreview
import org.my.drivexcel.ui.theme.DriveXcelAppTheme
import org.my.drivexcel.ui.screens.events.EventsUIAction
import org.my.drivexcel.ui.utils.createGradientImageByteArray
import org.my.drivexcel.ui.utils.nav.phoneWindowSizeClassPreviewProvider

@Composable
fun EventListItemComponent(
    modifier: Modifier = Modifier,
    eventId: String,
    byteArray: ByteArray?,
    eventName: String,
    isEditingMode: Boolean,
    onAction: (EventsUIAction) -> Unit
) {

    val shape = remember { RoundedCornerShape(8.dp) }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = shape
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.surfaceContainerHighest,
                shape
            )
            .clip(shape)
            .clickable {
                onAction(EventsUIAction.EventPressed(eventId))
            }
            .padding(0.dp),
    ) {

        Row{
            AsyncImage(
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp)
                    .clip(RoundedCornerShape(bottomEnd = if (isEditingMode) 8.dp else 0.dp)),
                model = byteArray,
                fallback = painterResource(Res.drawable.preview),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )


            Column(modifier = Modifier.animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )) {
                AnimatedVisibility(
                    visible = isEditingMode,
                    enter = fadeIn() + slideInHorizontally { it } + scaleIn(),
                    exit = fadeOut() + slideOutHorizontally { it } + scaleOut()
                ) {
                    Column {
                        IconButton(onClick = {
                            onAction(EventsUIAction.UpdateEvent(eventId))
                        }) {
                            Icon(Icons.Default.Create, null)
                        }

                        IconButton(onClick = {
                            onAction(EventsUIAction.OnDeleteClicked(eventId))
                        }) {
                            Icon(Icons.Default.Delete, null)
                        }
                    }
                }

            }
        }



        Text(
            modifier = Modifier.padding(16.dp),
            text = eventName,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DefaultPreviewItem(
    byteArray: ByteArray? = createGradientImageByteArray()
) = EventListItemComponent(
    eventId = "",
    byteArray = byteArray,
    eventName = "Мероприятие",
    isEditingMode = false,
    onAction = {}
)

@Preview
@Composable
private fun PreviewEmptyNight() = DriveXcelAppTheme(
    darkTheme = true,
    phoneWindowSizeClassPreview,
    windowSizeClassProvider = phoneWindowSizeClassPreviewProvider

) {
    DefaultPreviewItem()
}

@Preview
@Composable
private fun PreviewEmptyLight() = DriveXcelAppTheme(
    darkTheme = false,
    phoneWindowSizeClassPreview,
    windowSizeClassProvider = phoneWindowSizeClassPreviewProvider

) {
    DefaultPreviewItem()
}

@Preview
@Composable
private fun PreviewWithImageNight() = DriveXcelAppTheme(
    darkTheme = true,
    phoneWindowSizeClassPreview,
    windowSizeClassProvider = phoneWindowSizeClassPreviewProvider

) {
    DefaultPreviewItem(
        byteArray = null
    )
}

@Preview
@Composable
private fun PreviewWithImageLight() = DriveXcelAppTheme(
    darkTheme = false,
    phoneWindowSizeClassPreview,
    windowSizeClassProvider = phoneWindowSizeClassPreviewProvider

) {
    DefaultPreviewItem(
        byteArray = null
    )
}