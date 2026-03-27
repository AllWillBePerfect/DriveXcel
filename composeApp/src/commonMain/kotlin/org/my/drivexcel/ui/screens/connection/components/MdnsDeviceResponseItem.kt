package org.my.drivexcel.ui.screens.connection.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

@Composable
fun MdnsDeviceResponseItem(
    device: MdnsService.Device,
    onApprove: () -> Unit,
    onReject: () -> Unit,
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
            SingleChoiceSegmentedButton(
                onApprove = onApprove,
                onReject = onReject
            )
        }
    }

}

@Composable
fun SingleChoiceSegmentedButton(
    modifier: Modifier = Modifier,
    onApprove: () -> Unit,
    onReject: () -> Unit,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Принять", "Отклонить")

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { if (index == 0) onApprove() else onReject() },
                selected = false,
                label = { Text(label) }
            )
        }
    }
}

@Composable
@Preview
private fun PreviewNightPhone() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = phoneWindowSizeClassPreview
) {
    MdnsDeviceResponseItem(
        device = MdnsService.Device.default(),
        onApprove = {},
        onReject = {}
    )
}

@Preview
@Composable
private fun PreviewLightPhone() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = phoneWindowSizeClassPreview

) {
    MdnsDeviceResponseItem(
        device = MdnsService.Device.default(),
        onApprove = {},
        onReject = {}
    )
}