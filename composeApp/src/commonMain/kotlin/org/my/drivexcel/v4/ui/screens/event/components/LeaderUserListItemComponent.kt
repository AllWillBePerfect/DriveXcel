package org.my.drivexcel.v4.ui.screens.event.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.my.drivexcel.platform.utils.phoneWindowSizeClassPreview
import org.my.drivexcel.theme.DriveXcelAppTheme

@Composable
fun LeaderUserListItemComponent(
    userId: Int,
    userName: String
) {
    ListItem(
        modifier = Modifier.clickable {},
        headlineContent = {
            Text(
                text = userName
            )
        },
        supportingContent = {
            Text(
                text = userId.toString()
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(90.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center,

                ) {
                Text(
                    text = userName.first().uppercase()
                )
            }
        }
    )
}

@Composable
private fun DefaultPreviewItem() = LeaderUserListItemComponent(
    userId = 123421,
    userName = "Jane Doe"
)

@Preview
@Composable
private fun PreviewNight() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = phoneWindowSizeClassPreview
) {
    DefaultPreviewItem()
}

@Preview
@Composable
private fun PreviewLight() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = phoneWindowSizeClassPreview
) {
    DefaultPreviewItem()
}