package org.my.drivexcel.v4.ui.screens.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_arrow_back
import drivexcel.composeapp.generated.resources.ic_error
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.v4.ui.components.InfoMessageComponent
import org.my.drivexcel.v4.ui.models.UiIcon
import org.my.drivexcel.v4.ui.models.UiText
import org.my.drivexcel.v4.ui.screens.event.models.EventUiAction
import org.my.drivexcel.v4.ui.screens.event.models.EventUiEvent
import org.my.drivexcel.v4.ui.screens.event.models.EventUiState
import org.my.drivexcel.v4.ui.screens.event.utils.EventTabs
import org.my.drivexcel.v4.ui.screens.event.utils.toName
import org.my.drivexcel.v4.ui.screens.importt.ImportRoute
import org.my.drivexcel.v4.ui.screens.users.UsersRoute

@Composable
fun EventRoute(
    viewModel: EventViewModel = koinViewModel(),
    onBackClicked: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EventScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
    )

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                EventUiEvent.NavigateBack -> onBackClicked()
            }
        }
    }

}

@Composable
private fun EventScreen(
    uiState: EventUiState,
    onAction: (EventUiAction) -> Unit,

    ) {
    EventWrapper(
        uiState = uiState,
        onAction = onAction,
        content = { innerPadding ->
            EventContent(
                innerPadding = innerPadding,
                uiState = uiState,
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventWrapper(
    uiState: EventUiState,
    onAction: (EventUiAction) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = uiState.eventName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(EventUiAction.OnBackClicked) }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
private fun EventContent(
    innerPadding: PaddingValues,
    uiState: EventUiState,
) {

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    if (!uiState.isLoadingError) {
        Column(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        ) {

            PrimaryTabRow(selectedTab) {
                EventTabs.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(tab.toName()) }
                    )
                }
            }

            when (EventTabs.entries[selectedTab]) {
                EventTabs.Users -> {
                    UsersRoute(
                        eventId = uiState.id
                    )
                }

                EventTabs.Import -> {
                    ImportRoute(
                        eventId = uiState.id
                    )
                }
            }
        }
    } else {
        InfoMessageComponent(
            modifier = Modifier.padding(innerPadding),
            icon = UiIcon.Drawable(Res.drawable.ic_error),
            text = UiText.Text("Произошла ошибка при загрузке мероприятия")
        )
    }
}

/*
@Composable
private fun EventContent(
    innerPadding: PaddingValues,
    uiState: EventUiState,
    onAction: (EventUiAction) -> Unit,
    navHostController: NavHostController,
) {

    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
    ) {

        PrimaryTabRow(selectedTab) {
            EventTabs.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        selectedTab = index
                        val route = when (tab) {
                            EventTabs.Users -> EventRoutes.Users.createRoute(uiState.id)
                            EventTabs.Import -> EventRoutes.Import.createRoute(uiState.id)
                        }

                        navHostController.navigate(route) {
                            popUpTo(navHostController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    text = { Text(tab.toName()) }
                )
            }
        }

        NavHost(
            navHostController,
            EventRoutes.Users.createRoute(uiState.id)
        ) {
            composable(
                route = EventRoutes.Users.route,
                arguments = listOf(
                    navArgument(EVENT_ID_NAV_ARGUMENT) { NavType.StringType }
                )
            ) {
                UsersRoute()
            }
            composable(
                route = EventRoutes.Import.route,
                arguments = listOf(
                    navArgument(EVENT_ID_NAV_ARGUMENT) { NavType.StringType }
                )
            ) {
                ImportRoute()
            }
        }
    }
}

*/
