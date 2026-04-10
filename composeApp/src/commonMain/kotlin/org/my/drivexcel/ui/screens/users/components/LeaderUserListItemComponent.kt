package org.my.drivexcel.ui.screens.users.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.my.drivexcel.platform.phoneWindowSizeClassPreview
import org.my.drivexcel.ui.theme.DriveXcelAppTheme
import org.my.drivexcel.domain.model.LeaderUserDomainModel
import org.my.drivexcel.ui.utils.nav.windowSizeClassPreviewProvider

@Composable
fun LeaderUserListItemComponent(

    leaderItem: LeaderUserDomainModel,
    onItemClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.clickable { onItemClick() }
    ) {
        ListItem(
            modifier = Modifier,
            headlineContent = {
                Text(
                    text = leaderItem.fullName
                )
            },
            supportingContent = {
                Text(
                    text = leaderItem.id.toString()
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
                        text = leaderItem.fullName.first().uppercase()
                    )
                }
            },
            trailingContent = {
                val rotation by animateFloatAsState(
                    targetValue = if (isExpanded) 180f else 0f,
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    ),
                    label = "arrow_rotation"
                )

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }
        )
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .border(1.dp, MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                Text(leaderItem.age.toString())
                Text(leaderItem.company.toString())
                Text(leaderItem.jobTitle.toString())
                Text(leaderItem.role)
                Text(leaderItem.format)

            }
        }

    }
}

@Composable
private fun DefaultPreviewItem() = LeaderUserListItemComponent(

    leaderItem = LeaderUserDomainModel.generateUsers(1).first(),
    onItemClick = {}
)

@Preview
@Composable
private fun PreviewNight() = DriveXcelAppTheme(
    darkTheme = true,
    myWindowSizeClass = phoneWindowSizeClassPreview,
    windowSizeClassProvider = windowSizeClassPreviewProvider

) {
    DefaultPreviewItem()
}

@Preview
@Composable
private fun PreviewLight() = DriveXcelAppTheme(
    darkTheme = false,
    myWindowSizeClass = phoneWindowSizeClassPreview,
    windowSizeClassProvider = windowSizeClassPreviewProvider

) {
    DefaultPreviewItem()
}