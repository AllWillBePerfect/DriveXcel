package org.my.drivexcel.ui.screens.events.wide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.compose.koinInject
import org.my.drivexcel.platform.BackHandlerProvider
import org.my.drivexcel.ui.theme.LocalWindowSizeClass
import org.my.drivexcel.ui.platform.BoxDividerProvider
import org.my.drivexcel.ui.screens.event.EventRoute
import org.my.drivexcel.ui.screens.events.EventsRoute
import org.my.drivexcel.ui.utils.nav.Route

@Composable
fun EventsWithDetailsRoute(
    navController: NavController
) {

    EventsWithDetailsScreen(
        navController = navController
    )
}

@Composable
private fun EventsWithDetailsScreen(
    navController: NavController
) {
    EventsWithDetailsWrapper(
        content = { innerPadding ->
            EventsWithDetailsContent(
                innerPadding = innerPadding,
                navController = navController
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventsWithDetailsWrapper(
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold { innerPadding ->
        content(innerPadding)
    }
}

@Composable
private fun EventsWithDetailsContent(
    innerPadding: PaddingValues,
    navController: NavController,
    boxDividerProvider: BoxDividerProvider = koinInject(),
    backHandlerProvider: BackHandlerProvider = koinInject()
) {
    var eventId by rememberSaveable { mutableStateOf<String?>(null) }

    val windowSizeClass = LocalWindowSizeClass.current
    val isExpanded = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    if (isExpanded) {
        EventsTwoPane(
            eventId = eventId,
            onEventSelected = { eventId = it },
            onClearSelection = { eventId = null },
            navController = navController,
            boxDividerProvider = boxDividerProvider
        )
    } else {
        EventsSinglePane(
            eventId = eventId,
            onEventSelected = { eventId = it },
            onClearSelection = { eventId = null },
            navController = navController,
            backHandlerProvider = backHandlerProvider
        )
    }

}

@Composable
private fun EventsSinglePane(
    eventId: String?,
    onEventSelected: (String) -> Unit,
    onClearSelection: () -> Unit,
    navController: NavController,
    backHandlerProvider: BackHandlerProvider
) {

    if (eventId == null) {
        EventsRoute(
            onEventPressedNavigate = onEventSelected,
            onCreateEventNavigate = {
                navController.navigate(Route.CreateEvent) { launchSingleTop = true }
            },
            onUpdateEventNavigate = {
                navController.navigate(Route.UpdateEvent(it)) { launchSingleTop = true }
            }
        )
    } else {
        EventRoute(
            eventId = eventId,
            onBackClickedNavigate = onClearSelection
        )

        backHandlerProvider.BackHandler {
            onClearSelection()
        }
    }
}

@Composable
private fun EventsTwoPane(
    eventId: String?,
    onEventSelected: (String) -> Unit,
    onClearSelection: () -> Unit,
    navController: NavController,
    boxDividerProvider: BoxDividerProvider
) {

    var dividerPosition by remember { mutableFloatStateOf(0.35f) }
    val dividerWidth = 4.dp

    Row {

        EventsListPane(
            modifier = Modifier
                .weight(dividerPosition)
                .fillMaxHeight(),
            onEventSelected = onEventSelected,
            navController = navController
        )

        boxDividerProvider.BoxDivider(
            dividerWidth = dividerWidth,
            setOnDoubleTap = { dividerPosition = it },
            setOnDrag = {
                dividerPosition =
                    (dividerPosition + it / 1000f).coerceIn(0.2f, 0.8f)
            }
        )

        EventDetailsPane(
            modifier = Modifier
                .weight(1f - dividerPosition)
                .fillMaxHeight(),
            eventId = eventId,
            onBack = onClearSelection
        )
    }
}

@Composable
private fun EventsListPane(
    modifier: Modifier,
    onEventSelected: (String) -> Unit,
    navController: NavController
) {
    Box(modifier) {
        EventsRoute(
            onEventPressedNavigate = onEventSelected,
            onCreateEventNavigate = {
                navController.navigate(Route.CreateEvent) {
                    launchSingleTop = true
                }
            },
            onUpdateEventNavigate = {
                navController.navigate(Route.UpdateEvent(it)) {
                    launchSingleTop = true
                }
            }
        )
    }
}

@Composable
private fun EventDetailsPane(
    modifier: Modifier,
    eventId: String?,
    onBack: () -> Unit
) {
    Box(modifier) {
        if (eventId != null) {
            EventRoute(
                eventId = eventId,
                onBackClickedNavigate = onBack
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            )
        }
    }
}
