package org.my.drivexcel.ui.screens.home.details.subcontainers

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.my.drivexcel.platform.utils.phoneWindowSizeClassPreview
import org.my.drivexcel.theme.DriveXcelAppTheme

@Composable
fun MergeSubContainerItem() {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row {
            Icon(
                modifier = Modifier
                    .size(48.dp),
                painter = painterResource(Res.drawable.compose_multiplatform),
                contentDescription = null,
                tint = Color.Unspecified
            )

            Text(
                text = "Sample Text"
            )
        }
    }
}

@Preview
@Composable
private fun MergeSubContainerItemPreviewNight() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = phoneWindowSizeClassPreview
) {
    MergeSubContainerItem()
}

@Preview
@Composable
private fun MergeSubContainerItemPreviewLight() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = phoneWindowSizeClassPreview

) {
    MergeSubContainerItem()
}