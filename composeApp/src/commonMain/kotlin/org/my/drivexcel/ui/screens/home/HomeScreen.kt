package org.my.drivexcel.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.platform.utils.BackHandlerProvider
import org.my.drivexcel.platform.utils.WindowSize
import org.my.drivexcel.platform.utils.isCompact
import org.my.drivexcel.theme.LocalWindowSize
import org.my.drivexcel.ui.screens.home.details.DetailsScreen
import org.my.drivexcel.ui.screens.home.eventlist.EventsListScreen
import org.my.drivexcel.ui.screens.home.platform.BoxDividerProvider

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = koinViewModel(),
    backHandlerProvider: BackHandlerProvider = koinInject(),
    openDrawer: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()
    val windowSize = LocalWindowSize.current
    val showAppBar = windowSize.isCompact

    HomeScreen(
        uiState = uiState,
        backHandlerProvider = backHandlerProvider,
        windowSize = windowSize,
        showAppBar = showAppBar,
        onEventClick = viewModel::onEventClick,
        onEventClose = viewModel::onEventClose,
        openDrawer = openDrawer
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeViewModel.HomeUiState,
    backHandlerProvider: BackHandlerProvider,
    windowSize: WindowSize,
    showAppBar: Boolean,
    onEventClick: (String) -> Unit,
    onEventClose: () -> Unit,
    openDrawer: () -> Unit
) {

    val isExpanded = windowSize == WindowSize.Expanded
    val homeScreenType = getHomeScreenType(
        isExpanded = isExpanded,
        uiState = uiState
    )

    when (homeScreenType) {
        HomeScreenType.Home -> {
            EventsListScreen(
                uiState = uiState,
                showAppBar = showAppBar,
                onEventClick = onEventClick,
                openDrawer = openDrawer
            )
        }

        HomeScreenType.Details -> {
            DetailsScreen(
                uiState = uiState,
                onEventClose = onEventClose
            )

            backHandlerProvider.BackHandler {
                onEventClose()
            }

        }

        HomeScreenType.HomeWithDetails -> {
            HomeWithDetailsScreen(
                uiState = uiState,
                showAppBar = showAppBar,
                onEventClick = onEventClick,
                onEventClose = onEventClose,
                openDrawer = openDrawer
            )
        }
    }

}

@Composable
private fun HomeWithDetailsScreen(
    boxDividerProvider: BoxDividerProvider = koinInject(),
    uiState: HomeViewModel.HomeUiState,
    showAppBar: Boolean,
    onEventClick: (String) -> Unit,
    onEventClose: () -> Unit,
    openDrawer: () -> Unit
) {
    var dividerPosition by remember { mutableFloatStateOf(0.35f) } // начальное соотношение
    val dividerWidth = 4.dp

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Левая панель
        Box(
            modifier = Modifier
                .weight(dividerPosition)
                .fillMaxHeight()
        ) {
            EventsListScreen(
                uiState = uiState,
                showAppBar = showAppBar,
                onEventClick = onEventClick,
                openDrawer = openDrawer
            )
        }

        boxDividerProvider.BoxDivider(
            dividerWidth = dividerWidth,
            setOnDoubleTap = { dividerPosition = it },
            setOnDrag = {
                dividerPosition = (dividerPosition + it / 1000f).coerceIn(0.2f, 0.8f)
            }
        )

        // Правая панель

        Box(
            modifier = Modifier
                .weight(1f - dividerPosition)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            if (uiState is HomeViewModel.HomeUiState.Loaded) {
                if (uiState.isDetailsOpen) {
                    DetailsScreen(
                        uiState = uiState,
                        onEventClose = onEventClose
                    )
                }
            }
        }

    }
}


private enum class HomeScreenType {
    Home, Details, HomeWithDetails,
}

@Composable
private fun getHomeScreenType(
    isExpanded: Boolean,
    uiState: HomeViewModel.HomeUiState
): HomeScreenType = when (isExpanded) {
    true -> HomeScreenType.HomeWithDetails
    false -> when (uiState) {
        is HomeViewModel.HomeUiState.Loaded -> {
            if (uiState.isDetailsOpen)
                HomeScreenType.Details
            else HomeScreenType.Home
        }

        HomeViewModel.HomeUiState.Loading -> HomeScreenType.Home
    }
}

