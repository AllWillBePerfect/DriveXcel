package org.my.drivexcel.v4.ui.screens.importt.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_excel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.my.drivexcel.platform.utils.phoneWindowSizeClassPreview
import org.my.drivexcel.theme.DriveXcelAppTheme

@Composable
fun XlsListItem(
    fileName: String,
    onDeleteClicked: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable {},
        headlineContent = {
            Text(
                text = fileName,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_excel),
                contentDescription = null,
                tint = Color.Unspecified
            )
        },
        trailingContent = {
            IconButton(onClick = onDeleteClicked) {
                Icon(
                    Icons.Default.Delete,
                    null
                )
            }
        }
    )
}

@Composable
private fun DefaultPreviewItem() = XlsListItem(
    fileName = "Пользователи.xls",
    onDeleteClicked = {}
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
