package org.my.drivexcel.ui.screens.home.eventlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_menu
import drivexcel.composeapp.generated.resources.preview
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.my.drivexcel.platform.utils.phoneWindowSizeClassPreview
import org.my.drivexcel.theme.DriveXcelAppTheme

@Composable
internal fun EventListItem(
    name: String,
    imagePath: String?,
    isSelected: Boolean,
    onEventClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onEventRedactingButtonClick: () -> Unit
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

    var expanded by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier.fillMaxWidth()
//            .clip(RoundedCornerShape(8.dp))
            .then(
                if (isSelected) Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
//                    shape = RoundedCornerShape(8.dp)
                ) else Modifier
            )
            .clickable { onEventClick() }

    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth().height(160.dp)
//                .clip(RoundedCornerShape(bottomEnd = 10.dp, bottomStart = 10.dp)),
//            model = image ?: imageResource(Res.drawable.preview)
                    ,
            model = imagePath,
            fallback = painterResource(Res.drawable.preview),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .background(brush)
                .padding(8.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Box() {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_menu),
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {

                    DropdownMenuItem(
                        text = { Text("Редактировать") },
                        onClick = { onEventRedactingButtonClick() }
                    )

                    DropdownMenuItem(
                        text = { Text("Удалить") },
                        onClick = onDeleteButtonClick
                    )

                }
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
        imagePath = null,
        isSelected = true,
        onEventClick = {},
        onDeleteButtonClick = {},
        onEventRedactingButtonClick = {}
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
        imagePath = null,
        isSelected = false,
        onEventClick = {},
        onDeleteButtonClick = {},
        onEventRedactingButtonClick = {}
    )
}