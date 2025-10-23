package org.my.drivexcel.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.my.drivexcel.platform.datasources.UserSettings

@Composable
fun SettingsItemSection(
    name: String = "Настройки"
) {
    Text(
        modifier = Modifier
            .fillMaxWidth(),
        text = name,
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Start
    )
}

@Composable
fun SettingsNightModeItem(
    switchNightMode: (UserSettings.NightMode) -> Unit,
    currentNightMode: UserSettings.NightMode
) {

    var isVisible by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.padding(bottom = 8.dp),
        onClick = {}
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable { isVisible = !isVisible }
                .padding(horizontal = 16.dp)
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Res.drawable.compose_multiplatform),
                contentDescription = null,
                tint = Color.Unspecified
            )

            Text(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                text = "Тема приложения"
            )
        }
        AnimatedVisibility(
            visible = isVisible
        ) {
            Column(
                Modifier
                    .padding(bottom = 12.dp)
            ) {
                RadioButtonItem(
                    nightMode = UserSettings.NightMode.YES,
                    isSelected = currentNightMode == UserSettings.NightMode.YES,
                    onClick = { switchNightMode(UserSettings.NightMode.YES) }
                )
                RadioButtonItem(
                    nightMode = UserSettings.NightMode.NO,
                    isSelected = currentNightMode == UserSettings.NightMode.NO,
                    onClick = { switchNightMode(UserSettings.NightMode.NO) }
                )
                RadioButtonItem(
                    nightMode = UserSettings.NightMode.FOLLOW_SYSTEM,
                    isSelected = currentNightMode == UserSettings.NightMode.FOLLOW_SYSTEM,
                    onClick = { switchNightMode(UserSettings.NightMode.FOLLOW_SYSTEM) }
                )
            }
        }
    }
}

@Composable
private fun RadioButtonItem(
    nightMode: UserSettings.NightMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    val text = remember {
        when (nightMode) {
            UserSettings.NightMode.YES -> "Темная"
            UserSettings.NightMode.NO -> "Светлая"
            UserSettings.NightMode.FOLLOW_SYSTEM -> "Системная"
        }
    }

    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = onClick)
        Text(
            modifier = Modifier
                .padding()
                .weight(1f),
            text = text
        )
    }
}