package org.my.drivexcel.ui.screens.connection.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.my.drivexcel.MdnsService
import org.my.drivexcel.platform.utils.phoneWindowSizeClassPreview
import org.my.drivexcel.theme.DriveXcelAppTheme
import org.my.drivexcel.ui.components.LoadingButton

@Composable
fun MdnsDeviceConnectItem(
    device: MdnsService.Device,
    pendingClient: () -> Boolean,
    onClick: () -> Unit,
    sendMessage: (MdnsService.Device, String) -> Unit
) {

    var isLoading by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${device.host}:${device.port}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Column {
               if (!pendingClient()) {
                   LoadingButton(
                       text = "Подключиться",
                       isLoading = isLoading,
                       onClick = onClick
                   )
                   Button(onClick = { sendMessage(device, "MESSAGE!!!!") }) {
                       Text(
                           text = "Сообщение"
                       )
                   }
               } else {
                   CircularProgressIndicator()
               }
            }
        }
    }

}


@Composable
@Preview
private fun PreviewNightPhone() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = phoneWindowSizeClassPreview
) {
    MdnsDeviceConnectItem(
        device = MdnsService.Device.default(),
        pendingClient ={false} ,
        onClick = {},
        sendMessage = {_, _ ->}
    )
}

@Preview
@Composable
private fun PreviewLightPhone() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = phoneWindowSizeClassPreview

) {
    MdnsDeviceConnectItem(
        device = MdnsService.Device.default(),
        pendingClient = {false},
        onClick = {},
        sendMessage = {_, _ ->}
    )
}