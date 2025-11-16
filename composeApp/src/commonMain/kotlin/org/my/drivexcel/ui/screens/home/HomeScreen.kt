package org.my.drivexcel.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
    onEventRedacting: (String) -> Unit,
    openDrawer: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()
    val windowSize = LocalWindowSize.current
    val showAppBar = windowSize.isCompact

    val detailsTabsNavController = rememberNavController()


    HomeScreen(
        uiState = uiState,
        detailsTabsNavController = detailsTabsNavController,
        backHandlerProvider = backHandlerProvider,
        windowSize = windowSize,
        showAppBar = showAppBar,
        onEventClick = viewModel::onEventClick,
        onEventDelete = viewModel::onEventDelete,
        onEventRedacting = onEventRedacting,
        onEventClose = viewModel::onEventClose,
        switchTabOnDetails = viewModel::switchTab,
        openDrawer = openDrawer
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeViewModel.HomeUiState,
    detailsTabsNavController: NavHostController,
    backHandlerProvider: BackHandlerProvider,
    windowSize: WindowSize,
    showAppBar: Boolean,
    onEventClick: (String) -> Unit,
    onEventDelete: (String) -> Unit,
    onEventRedacting: (String) -> Unit,
    onEventClose: () -> Unit,
    switchTabOnDetails: (Int) -> Unit,
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
                windowSize = windowSize,
                showAppBar = showAppBar,
                onEventClick = onEventClick,
                onEventRedacting = onEventRedacting,
                onEventDelete = onEventDelete,
                openDrawer = openDrawer
            )
        }

        HomeScreenType.Details -> {
            DetailsScreen(
                uiState = uiState,
                detailsTabsNavController = detailsTabsNavController,
                onEventClose = onEventClose,
                switchTabOnDetails = switchTabOnDetails
            )

            backHandlerProvider.BackHandler {
                onEventClose()
            }

        }

        HomeScreenType.HomeWithDetails -> {
            HomeWithDetailsScreen(
                uiState = uiState,
                detailsTabsNavController = detailsTabsNavController,
                windowSize = windowSize,
                showAppBar = showAppBar,
                onEventClick = onEventClick,
                onEventDelete = onEventDelete,
                onEventRedacting = onEventRedacting,
                onEventClose = onEventClose,
                switchTabOnDetails = switchTabOnDetails,
                openDrawer = openDrawer
            )
        }
    }

}

@Composable
private fun HomeWithDetailsScreen(
    boxDividerProvider: BoxDividerProvider = koinInject(),
    uiState: HomeViewModel.HomeUiState,
    detailsTabsNavController: NavHostController,
    windowSize: WindowSize,
    showAppBar: Boolean,
    onEventClick: (String) -> Unit,
    onEventDelete: (String) -> Unit,
    onEventRedacting: (String) -> Unit,
    onEventClose: () -> Unit,
    switchTabOnDetails: (Int) -> Unit,
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
                windowSize = windowSize,
                showAppBar = showAppBar,
                onEventClick = onEventClick,
                onEventDelete = onEventDelete,
                onEventRedacting = onEventRedacting,
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
//                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            if (uiState is HomeViewModel.HomeUiState.Loaded) {
                if (uiState.isDetailsOpen) {
                    DetailsScreen(
                        uiState = uiState,
                        detailsTabsNavController = detailsTabsNavController,
                        onEventClose = onEventClose,
                        switchTabOnDetails = switchTabOnDetails
                    )
                } else {
                    Scaffold { innerPadding ->
                        Box(
                            modifier = Modifier.fillMaxSize()
                                .padding(innerPadding)
//                                .padding(16.dp)
//                                .clip(RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                            contentAlignment = Alignment.Center,

                            ) {
                            Column {
                                Text(
                                    text = "Выберите мероприятие из списка",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.headlineLarge
                                )
                            }
                        }
                    }

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

