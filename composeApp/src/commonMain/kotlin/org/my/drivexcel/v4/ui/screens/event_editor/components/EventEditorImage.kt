package org.my.drivexcel.v4.ui.screens.event_editor.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_close
import drivexcel.composeapp.generated.resources.ic_image_upload
import drivexcel.composeapp.generated.resources.preview
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.my.drivexcel.platform.utils.phoneWindowSizeClassPreview
import org.my.drivexcel.theme.DriveXcelAppTheme
import org.my.drivexcel.v4.ui.screens.event_editor.EventEditingAction
import org.my.drivexcel.v4.ui.screens.event_editor.EventEditorUiState
import org.my.drivexcel.v4.ui.screens.event_editor.ImageBytes
import org.my.drivexcel.v4.ui.utils.createGradientImageByteArray


@Composable
internal fun EventEditorImageComponent(
    onAction: (EventEditingAction) -> Unit,
    uiState: EventEditorUiState
) {

    val hasImage = uiState.userImage != null

    Box(Modifier.fillMaxWidth()) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { if (uiState.userImage == null) onAction(EventEditingAction.ImageClicked) }
                .alpha(if (hasImage) 1f else 0.3f)
            ,
            model = uiState.userImage?.bytes,
            fallback = painterResource(Res.drawable.preview),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        if (!hasImage) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(64.dp),
                painter = painterResource(Res.drawable.ic_image_upload),
                contentDescription = "Удалить изображение"
            )
        }

        if (hasImage) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                ),
                onClick = { onAction(EventEditingAction.ImageRemoved) },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_close),
                    contentDescription = "Удалить изображение"
                )
            }
        }

    }
}

@Composable
private fun defaultPreview(bytes: ByteArray? = null) = EventEditorImageComponent(
    onAction = {},
    uiState = EventEditorUiState(
        userImage = bytes?.let { ImageBytes(bytes = it) }
    )
)

@Preview
@Composable
private fun PreviewImageNight() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = phoneWindowSizeClassPreview

) {
    defaultPreview(
        bytes = createGradientImageByteArray()
    )
}

@Preview
@Composable
private fun PreviewImageLight() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = phoneWindowSizeClassPreview
) {
    defaultPreview(
        bytes = createGradientImageByteArray()
    )
}

@Preview
@Composable
private fun PreviewEmptyNight() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = phoneWindowSizeClassPreview

) {
    defaultPreview()
}

@Preview
@Composable
private fun PreviewEmptyLight() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = phoneWindowSizeClassPreview
) {
    defaultPreview()
}

/*
@Composable
fun rememberDrawableBytes(resource: DrawableResource): State<ByteArray?> {

    val environment = rememberResourceEnvironment()

    return produceState<ByteArray?>(null, resource) {
        value = getDrawableResourceBytes(environment, resource)
    }
}*/


