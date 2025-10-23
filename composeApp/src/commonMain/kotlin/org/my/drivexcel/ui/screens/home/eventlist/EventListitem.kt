package org.my.drivexcel.ui.screens.home.eventlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_menu
import drivexcel.composeapp.generated.resources.okey
import drivexcel.composeapp.generated.resources.preview
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.my.drivexcel.platform.utils.phoneWindowSizeClassPreview
import org.my.drivexcel.theme.DriveXcelAppTheme

@Composable
internal fun EventListItem(
    name: String,
    image: ImageBitmap?,
    onEventClick: () -> Unit
) {

    val surfaceColor = MaterialTheme.colorScheme.surfaceContainer

    val brush = remember {
        Brush.linearGradient(
            colors = listOf(
                surfaceColor.copy(alpha = 0.3f),
                surfaceColor.copy(alpha = 1f),
            ),
            start = Offset(3000f, 0f),  // точка сверху справа
            end = Offset(0f, 100f)  // точка снизу справа
        )
    }

    Card(

    ) { }

    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onEventClick() }

    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth().height(160.dp)
                .clip(RoundedCornerShape(bottomEnd = 10.dp, bottomStart = 10.dp)),
            bitmap = image ?: imageResource(Res.drawable.preview),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .background(brush)
                .padding(8.dp)
                .align(Alignment.BottomCenter)

        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(Res.drawable.ic_menu),
                    contentDescription = null
                )
            }
        }


    }
}

@Preview
@Composable
private fun EventItemNight() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = phoneWindowSizeClassPreview
) {
    EventListItem(
        name = "Лекции Непомнящего",
        image = null,
        onEventClick = {}
    )
}

@Preview
@Composable
private fun EventItemLight() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = phoneWindowSizeClassPreview

) {
    EventListItem(
        name = "Лекции Непомнящего",
        image = null,
        onEventClick = {}
    )
}