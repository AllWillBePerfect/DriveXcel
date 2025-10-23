package org.my.drivexcel.ui.screens.home.eventlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_menu
import org.jetbrains.compose.resources.painterResource
import org.my.drivexcel.platform.utils.WindowSize
import org.my.drivexcel.platform.utils.isCompact
import org.my.drivexcel.platform.utils.phoneWindowSizeClassPreview
import org.my.drivexcel.theme.DriveXcelAppTheme
import org.my.drivexcel.theme.PhonePreview
import org.my.drivexcel.ui.screens.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsListScreen(
    uiState: HomeViewModel.HomeUiState,
    windowSize: WindowSize,
    showAppBar: Boolean,
    onEventClick: (String) -> Unit,
    onEventDelete: (String) -> Unit,
    onEventRedacting: (String) -> Unit,
    openDrawer: () -> Unit,
) {

    EventsListContainer(
        uiState = uiState,
        showAppBar = showAppBar,
        openDrawer = openDrawer,
        content = { uiState, innerPadding, modifier ->
            EventsListContent(
                uiState = uiState,
                windowSize = windowSize,
                contentModifier = modifier,
                innerPadding = innerPadding,
                onEventClick = onEventClick,
                onEventDelete = onEventDelete,
                onEventRedacting = onEventRedacting
            )
        }
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventsListContainer(
    uiState: HomeViewModel.HomeUiState,
    showAppBar: Boolean,
    openDrawer: () -> Unit,
    content: @Composable (
        uiState: HomeViewModel.HomeUiState.Loaded,
        innerPadding: PaddingValues,
        modifier: Modifier,
    ) -> Unit
) {

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)

    Scaffold(
        topBar = {
            if (showAppBar) {
                EventsListTopAppBar(
                    topAppBarState = topAppBarState,
                    openDrawer = openDrawer
                )
            }
        }
    ) { innerPadding ->
        val contentModifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)

        when (uiState) {
            is HomeViewModel.HomeUiState.Loaded -> content(
                uiState,
                innerPadding,
                contentModifier
            )

            HomeViewModel.HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }


    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EventsListTopAppBar(
    topAppBarState: TopAppBarState,
    openDrawer: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text("HomeAppBar")
        },
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
            topAppBarState
        ),
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    painter = painterResource(Res.drawable.ic_menu),
                    contentDescription = null
                )
            }
        }

    )
}

@Composable
private fun EventsListContent(
    uiState: HomeViewModel.HomeUiState.Loaded,
    windowSize: WindowSize,
    @Suppress("ModifierParameter") contentModifier: Modifier,
    innerPadding: PaddingValues,
    onEventClick: (String) -> Unit,
    onEventDelete: (String) -> Unit,
    onEventRedacting: (String) -> Unit,
) {
    val isCompat = windowSize.isCompact
    val columnPadding = PaddingValues(
        top = innerPadding.calculateTopPadding(),
        bottom = innerPadding.calculateBottomPadding(),
        start = if (isCompat) 16.dp else 0.dp,
        end = if (isCompat) 16.dp else 0.dp,
    )

    LazyColumn(
//        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .then(contentModifier),
        contentPadding = columnPadding
    ) {

//                    item {
//                        Text(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 64.dp)
//                            ,
//                            text = "Мероприятия",
//                            style = MaterialTheme.typography.headlineLarge,
//                            textAlign = TextAlign.Center
//                        )
//                    }

        itemsIndexed(items = uiState.eventsHome) { index, item ->
            EventListItem(
//                name = "[$index] Директория: ${item.id}",
                name = item.name,
                imagePath = item.imagePath,
                isSelected = item.isSelected,
                onEventClick = { onEventClick(item.id) },
                onDeleteButtonClick = { onEventDelete(item.id) },
                onEventRedactingButtonClick = { onEventRedacting(item.id) }
            )
//            Card(
//                border = if (item.isSelected) BorderStroke(
//                    1.dp,
//                    MaterialTheme.colorScheme.primary
//                ) else null,
//                onClick = { onEventClick(item.id) },
//                shape = RoundedCornerShape(0.dp)
//            ) {
//                Text(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    text = "[$index] Директория: ${item.id}"
//                )
//            }
        }
    }
}

@PhonePreview
@Composable
private fun EventsListScreenPreviewNight() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = phoneWindowSizeClassPreview
) {


    EventsListScreen(
        uiState = HomeViewModel.HomeUiState.createHomeDefault(),
        windowSize = WindowSize.Compact,
        showAppBar = true,
        onEventClick = {},
        openDrawer = {},
        onEventDelete = {},
        onEventRedacting = {}
    )

}

@PhonePreview
@Composable
private fun EventsListScreenPreviewLight() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = phoneWindowSizeClassPreview
) {


    EventsListScreen(
        uiState = HomeViewModel.HomeUiState.createHomeDefault(),
        windowSize = WindowSize.Compact,
        showAppBar = true,
        onEventClick = {},
        openDrawer = {},
        onEventDelete = {},
        onEventRedacting = {}

    )

}