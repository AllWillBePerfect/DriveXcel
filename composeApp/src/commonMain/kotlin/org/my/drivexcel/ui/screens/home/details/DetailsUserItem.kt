package org.my.drivexcel.ui.screens.home.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.my.drivexcel.platform.utils.LeaderUser
import org.my.drivexcel.platform.utils.phoneWindowSizeClassPreview
import org.my.drivexcel.theme.DriveXcelAppTheme

@Composable
fun DetailsUserItem(
    leaderUser: LeaderUser
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {},
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(90.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center,

                ) {
                Text(
                    text = leaderUser.getFirstLetter.uppercase()
                )
            }

            Column {
                Text(text = leaderUser.fullName)
            }
        }
    }
}

@Preview
@Composable
private fun DetailsUserItemPreviewNight() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = phoneWindowSizeClassPreview
) {
    DetailsUserItem(
        leaderUser = LeaderUser.createDefault()
    )
}

@Preview
@Composable
private fun DetailsUserItemPreviewLight() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = phoneWindowSizeClassPreview

) {
    DetailsUserItem(
        leaderUser = LeaderUser.createDefault()
    )
}