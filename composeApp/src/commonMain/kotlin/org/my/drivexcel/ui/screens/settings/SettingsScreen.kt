package org.my.drivexcel.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import drivexcel.composeapp.generated.resources.ic_arrow_back
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.platform.datasources.UserSettings
import org.my.drivexcel.platform.utils.BackHandlerProvider
import org.my.drivexcel.platform.utils.WindowSizeClassPreview
import org.my.drivexcel.platform.utils.isWide
import org.my.drivexcel.theme.DesktopPreview
import org.my.drivexcel.theme.DriveXcelAppTheme
import org.my.drivexcel.theme.LocalWindowSize
import org.my.drivexcel.theme.PhonePreview
import org.my.drivexcel.theme.TabletPreview

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = koinViewModel(),
    backHandlerProvider: BackHandlerProvider = koinInject(),
    onBackPressed: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    backHandlerProvider.BackHandler {
        onBackPressed()
    }


    SettingsScreen(
        uiState = uiState,
        switchNightMode = viewModel::switchNightMode,
        onBackPressed = onBackPressed
    )
}

@Composable
private fun SettingsScreen(
    uiState: SettingsViewModel.SettingsUiState,
    switchNightMode: (UserSettings.NightMode) -> Unit,
    onBackPressed: () -> Unit
) {

    SettingsContainer(
        onBackPressed = onBackPressed,
        content = { innerPadding ->
            SettingsLayout(
                uiState = uiState,
                innerPadding = innerPadding,
                switchNightMode = switchNightMode
            )
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContainer(
    onBackPressed: () -> Unit,

    content: @Composable (
        PaddingValues
    ) -> Unit
) {

    val windowSize = LocalWindowSize.current
    val isWide = windowSize.isWide

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                },
                navigationIcon = {
                    if (!isWide) {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_arrow_back),
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }

}

@Composable
private fun SettingsLayout(
    uiState: SettingsViewModel.SettingsUiState,
    innerPadding: PaddingValues,
    switchNightMode: (UserSettings.NightMode) -> Unit

) {

    val windowSize = LocalWindowSize.current
    val isDesktopSize = windowSize.isWide

    val content = settingsLazyColumnContent(
        uiState = uiState,
        switchNightMode = switchNightMode
    )

    if (isDesktopSize) {
        ExpandableSettingsScreen(
            content = content,
            innerPadding = innerPadding,
        )
    } else {
        CompactSettingsScreen(
            content = content,
            innerPadding = innerPadding
        )
    }
}

@Composable
private fun CompactSettingsScreen(
    content: LazyListScope.() -> Unit,
    innerPadding: PaddingValues
) {

    Column {
        /*Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp),
            text = "Настройки",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )*/
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentPadding = innerPadding,
        ) {
            content()
        }
    }

}

@Composable
private fun ExpandableSettingsScreen(
    content: LazyListScope.() -> Unit,
    innerPadding: PaddingValues
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /*Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp),
            text = "Настройки",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )*/
        LazyColumn(
            modifier = Modifier.width(600.dp).padding(horizontal = 16.dp),
            contentPadding = innerPadding,

            ) {
            content()
        }
    }
}

@Composable
private fun settingsLazyColumnContent(
    uiState: SettingsViewModel.SettingsUiState,
    switchNightMode: (UserSettings.NightMode) -> Unit
): LazyListScope.() -> Unit = {

    when (uiState) {
        SettingsViewModel.SettingsUiState.Loading -> {}
        is SettingsViewModel.SettingsUiState.Loaded -> {
            item {
                SettingsNightModeItem(
                    switchNightMode = switchNightMode,
                    currentNightMode = uiState.nightMode
                )
            }
            item { SettingsItemSection() }
            item { SettingsItemItem() }
            item { SettingsItemItem() }
            item { SettingsItemSection() }
            item { SettingsItemItem() }
            item { SettingsItemItem() }
            items(20) {
                SettingsItemItem()
            }
        }
    }


}


@Composable
private fun SettingsItemSection() {
    Text(
        modifier = Modifier
            .fillMaxWidth(),
        text = "Настройки",
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Start
    )
}

@Composable
private fun SettingsItemItem() {
    Card(
        modifier = Modifier.padding(bottom = 8.dp),
        onClick = {}
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(vertical = 16.dp)
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Res.drawable.compose_multiplatform),
                contentDescription = null,
                tint = Color.Unspecified
            )


            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = "Настройки"
            )
        }
    }
}

@Composable
private fun SettingsSwitchItem() {
    Card(
        modifier = Modifier.padding(bottom = 8.dp),
        onClick = {}
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.padding(start = 16.dp).weight(1f),
                text = "Тема приложения"
            )

            Switch(
                checked = true,
                onCheckedChange = {}
            )
        }
    }
}

@Composable
private fun SettingsNightModeItem(
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

@PhonePreview
@Composable
private fun SettingsScreenPreviewNightPhone() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = WindowSizeClassPreview()
) {
    SettingsScreenDefaultForPreview()
}

@PhonePreview
@Composable
private fun SettingsScreenPreviewLightPhone() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = WindowSizeClassPreview()

) {
    SettingsScreenDefaultForPreview()
}

@TabletPreview
@Composable
private fun SettingsScreenPreviewNightTablet() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = WindowSizeClassPreview(
        widthDp = 840.dp,
        heightDp = 800.dp
    )
) {
    SettingsScreenDefaultForPreview()
}

@TabletPreview
@Composable
private fun SettingsScreenPreviewLightTablet() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = WindowSizeClassPreview(
        widthDp = 840.dp,
        heightDp = 800.dp
    )

) {
    SettingsScreenDefaultForPreview()
}


@DesktopPreview
@Composable
private fun SettingsScreenPreviewNightDesktop() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = WindowSizeClassPreview(
        widthDp = 1280.dp,
        heightDp = 800.dp
    )
) {
    SettingsScreenDefaultForPreview()
}

@DesktopPreview
@Composable
private fun SettingsScreenPreviewLightDesktop() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = WindowSizeClassPreview(
        widthDp = 1280.dp,
        heightDp = 800.dp
    )

) {
    SettingsScreenDefaultForPreview()
}

@Composable
private fun SettingsScreenDefaultForPreview() {
    SettingsScreen(
        uiState = SettingsViewModel.SettingsUiState.Loaded(UserSettings.NightMode.FOLLOW_SYSTEM),
        switchNightMode = {},
        onBackPressed = {}
    )
}




