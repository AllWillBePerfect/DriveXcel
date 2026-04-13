package org.my.drivexcel.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.base.domain.model.NightModeModel
import org.my.drivexcel.ui.components.AlertDialogComponent
import org.my.drivexcel.ui.components.CenteredContainerComponent

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = koinViewModel(),
    onUnauthorizeButtonClicked: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                SettingsUiEvent.NavigateToLoginGraph -> onUnauthorizeButtonClicked()
            }
        }
    }

    SettingsScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )

    if (uiState.isUnauthorizeDialogEnabled) {
        AlertDialogComponent(
            onDismissRequest = { viewModel.onAction(SettingsUiAction.CloseUnauthorizedDialog) },
            onConfirmation = { viewModel.onAction(SettingsUiAction.UnauthorizeUser)  },
            dialogTitle = "Подтвердите действие",
            dialogText = "Перейти на начальный экран?",
            icon = Icons.AutoMirrored.Filled.Logout
        )
    }
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    onAction: (SettingsUiAction) -> Unit
) {
    SettingsWrapper(
        content = { innerPadding ->
            SettingsContent(
                innerPadding = innerPadding,
                uiState = uiState,
                onAction = onAction
            )
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsWrapper(
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Настройки"
                    )
                }
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
private fun SettingsContent(
    innerPadding: PaddingValues,
    uiState: SettingsUiState,
    onAction: (SettingsUiAction) -> Unit
) {
    CenteredContainerComponent {
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn {
                settingsSection("Тема приложения") {
                    SectionItemNightMode(
                        nightModeModel = NightModeModel.NIGHT,
                        currentNightModeModel = uiState.nightModeModel,
                        onItemClick = { onAction(SettingsUiAction.SwitchNightMode(it)) }
                    )
                    SectionItemNightMode(
                        nightModeModel = NightModeModel.DAY,
                        currentNightModeModel = uiState.nightModeModel,
                        onItemClick = { onAction(SettingsUiAction.SwitchNightMode(it)) }
                    )
                    SectionItemNightMode(
                        nightModeModel = NightModeModel.FOLLOW_SYSTEM,
                        currentNightModeModel = uiState.nightModeModel,
                        onItemClick = { onAction(SettingsUiAction.SwitchNightMode(it)) }
                    )
                }

                settingsSection {

                    SectionItemWithIcon(
                        item = "Exit",
                        onClick = { onAction(SettingsUiAction.ShowUnauthorizedDialog) },
                        title = { "Перейти на начальный экран" },
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                null
                            )
                        }
                    )
                }
            }
        }
    }

}

private fun LazyListScope.settingsSection(
    title: String = "",
    content: @Composable ColumnScope.() -> Unit
) {
    item {
        Column {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )

            SectionContainer {
                content()
            }
        }
    }
}

@Composable
private fun SectionContainer(
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = shape
            )
    ) {
        content()
    }
}

@Composable
fun <T> SectionItemWithIcon(
    item: T,
    onClick: (T) -> Unit,
    title: (T) -> String,
    icon: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    Column(modifier) {
        ListItem(
            colors = ListItemDefaults.colors().copy(containerColor = Color.Transparent),
            modifier = Modifier
                .clickable { onClick(item) },
            headlineContent = {
                Text(title(item))
            },
            leadingContent = {
                icon(item)
            }
        )

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        }
    }
}

@Composable
private fun SectionItemNightMode(
    nightModeModel: NightModeModel,
    currentNightModeModel: NightModeModel,
    onItemClick: (NightModeModel) -> Unit,
    showDivider: Boolean = true
) {
    val isCurrentItem = currentNightModeModel == nightModeModel
    Column {
        ListItem(
            colors = ListItemDefaults.colors().copy(containerColor = Color.Transparent),
            modifier = Modifier
                .clickable { onItemClick(nightModeModel) },
            headlineContent = { Text(nightModeModel.toName()) },
            trailingContent = {
                Switch(
                    checked = isCurrentItem,
                    onCheckedChange = null
                )
            }
        )
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        }
    }
}

@Composable
private fun <T> SectionSwitchItem(
    item: T,
    selectedItem: T,
    onItemClick: (T) -> Unit,
    title: (T) -> String,
    showDivider: Boolean = true
) {
    val isSelected = item == selectedItem

    Column {
        ListItem(
            colors = ListItemDefaults.colors().copy(containerColor = Color.Transparent),
            modifier = Modifier
                .clickable { onItemClick(item) },
            headlineContent = {
                Text(title(item))
            },
            trailingContent = {
                Switch(
                    checked = isSelected,
                    onCheckedChange = null
                )
            }
        )

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        }
    }
}

