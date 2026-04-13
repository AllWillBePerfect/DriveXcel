package org.my.drivexcel.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.my.drivexcel.ui.theme.DriveXcelAppTheme
import org.my.drivexcel.ui.utils.nav.phoneWindowSizeClassPreviewProvider
import org.my.drivexcel.platform.phoneWindowSizeClassPreview


@Composable
fun AlertDialogComponent(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = null)
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Подтвердить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Отменить")
            }
        }
    )
}

@Composable
private fun DefaultPreviewItem() = AlertDialogComponent(
    onDismissRequest = {},
    onConfirmation = {},
    dialogTitle = "Удалить мероприятие",
    dialogText = "Вы действительно хотите удалить мероприятие?\nВы уже не сможете его восстановить.",
    icon = Icons.Default.Delete
)

@Preview
@Composable
private fun PreviewNight() = DriveXcelAppTheme(
    darkTheme = true,
    myWindowSizeClass = phoneWindowSizeClassPreview,
    windowSizeClassProvider = phoneWindowSizeClassPreviewProvider

) {
    DefaultPreviewItem()
}

@Preview
@Composable
private fun PreviewLight() = DriveXcelAppTheme(
    darkTheme = false,
    myWindowSizeClass = phoneWindowSizeClassPreview,
    windowSizeClassProvider = phoneWindowSizeClassPreviewProvider

) {
    DefaultPreviewItem()
}