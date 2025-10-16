package org.my.drivexcel.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_arrow_back
import drivexcel.composeapp.generated.resources.compose_multiplatform
import drivexcel.composeapp.generated.resources.menu
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.platform.utils.BackHandlerProvider
import org.my.drivexcel.platform.utils.WindowSize
import org.my.drivexcel.platform.utils.WindowSizeClassPreview
import org.my.drivexcel.platform.utils.isCompact
import org.my.drivexcel.theme.DriveXcelAppTheme
import org.my.drivexcel.theme.LocalWindowSize
import org.my.drivexcel.theme.PhonePreview

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
    onEventClick: (Int) -> Unit,
    onEventClose: () -> Unit,
    openDrawer: () -> Unit
) {

    val isExpanded = windowSize != WindowSize.Compact
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenASample(
    uiState: HomeViewModel.HomeUiState,
    onEventClose: () -> Unit
) {


    when (uiState) {
        is HomeViewModel.HomeUiState.Home -> {
            val appBarText = when (uiState.selectedEvent) {
                is HomeViewModel.EventOnDetailsUiState.EventSelected -> uiState.selectedEvent.eventId
                HomeViewModel.EventOnDetailsUiState.NoEventSelected -> "None"
            }
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("Мероприятие: $appBarText") },
                    navigationIcon = {
                        IconButton(
                            onClick = onEventClose
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_arrow_back),
                                contentDescription = null
                            )
                        }
                    }
                )
                Column(Modifier) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = {}
                    ) { }

                    Spacer(Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = {}
                    ) { }
                }
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventsListScreen(
    uiState: HomeViewModel.HomeUiState,
    showAppBar: Boolean,
    onEventClick: (Int) -> Unit,
    openDrawer: () -> Unit
) {

    EventsListContainer(
        uiState = uiState,
        showAppBar = showAppBar,
        openDrawer = openDrawer,
        content = { uiState, innerPadding, modifier ->
            EventsListContent(
                uiState = uiState,
                contentModifier = modifier,
                innerPadding = innerPadding,
                onEventClick = onEventClick
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
        uiState: HomeViewModel.HomeUiState.Home,
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
            is HomeViewModel.HomeUiState.Home -> content(
                uiState,
                innerPadding,
                contentModifier
            )
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
                    painter = painterResource(Res.drawable.menu),
                    contentDescription = null
                )
            }
        }

    )
}

@Composable
private fun EventsListContent(
    uiState: HomeViewModel.HomeUiState.Home,
    @Suppress("ModifierParameter") contentModifier: Modifier,
    innerPadding: PaddingValues,
    onEventClick: (Int) -> Unit
) {
    LazyColumn(
//                    contentPadding = PaddingValues(vertical = 16.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
        modifier = Modifier
            .then(contentModifier)
//                        .padding(innerPadding)
        ,
        contentPadding = innerPadding
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

        itemsIndexed(uiState.eventsHome) { index, item ->
            Card(
                border = if (item.isSelected) BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary
                ) else null,
                onClick = { onEventClick(item.id) },
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = "[$index] Директория: ${item.id}"
                )
            }
        }
    }
}


@Composable
private fun DetailsScreen(
    uiState: HomeViewModel.HomeUiState,
    onEventClose: () -> Unit
) {
    ScreenASample(
        uiState = uiState,
        onEventClose = onEventClose
    )
}

@Composable
private fun HomeWithDetailsScreen(
    uiState: HomeViewModel.HomeUiState,
    showAppBar: Boolean,
    onEventClick: (Int) -> Unit,
    onEventClose: () -> Unit,
    openDrawer: () -> Unit
) {

    val leftWeight = remember { 0.35f }
    val rightWeight = remember { 0.65f }

    when (uiState) {
        is HomeViewModel.HomeUiState.Home -> {
            Row(modifier = Modifier.fillMaxSize()) {

                Box(Modifier.weight(leftWeight)) {
                    EventsListScreen(
                        uiState = uiState,
                        showAppBar = showAppBar,
                        onEventClick = onEventClick,
                        openDrawer = openDrawer
                    )
                }

                if (uiState.isDetailsOpen) {
                    Box(Modifier.weight(rightWeight)) {
                        DetailsScreen(
                            uiState = uiState,
                            onEventClose = onEventClose
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .weight(rightWeight)
                    )
                }

            }
        }
    }

}

@Composable
private fun EventItem() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth().height(200.dp),
            painter = painterResource(Res.drawable.compose_multiplatform),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Лекции Непомнящего",
                style = MaterialTheme.typography.titleLarge
            )
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
        is HomeViewModel.HomeUiState.Home -> {
            if (uiState.isDetailsOpen)
                HomeScreenType.Details
            else HomeScreenType.Home
        }
    }
}

@PhonePreview
@Composable
private fun HomeScreenPreviewNight() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = WindowSizeClassPreview()
) {
    HomeScreen(
        uiState = HomeViewModel.HomeUiState.createHomeDefault(),
        backHandlerProvider = object : BackHandlerProvider {
            @Composable
            override fun BackHandler(enabled: Boolean, onBack: () -> Unit) {

            }
        },
        windowSize = WindowSize.Compact,
        showAppBar = false,
        onEventClick = {},
        onEventClose = {},
        openDrawer = {}
    )
}

@PhonePreview()
@Composable
private fun HomeScreenPreviewLight() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = WindowSizeClassPreview()

) {
    HomeScreen(
        uiState = HomeViewModel.HomeUiState.createHomeDefault(),
        backHandlerProvider = object : BackHandlerProvider {
            @Composable
            override fun BackHandler(enabled: Boolean, onBack: () -> Unit) {

            }
        },
        windowSize = WindowSize.Compact,
        showAppBar = false,
        onEventClick = {},
        onEventClose = {},
        openDrawer = {}
    )
}

@Preview
@Composable
private fun EventItemNight() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = WindowSizeClassPreview()
) {
    EventItem()
}

@Preview
@Composable
private fun EventItemLight() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = WindowSizeClassPreview()

) {
    EventItem()

    //    Column {
//        Text(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 64.dp),
//            text = "Мероприятия",
//            style = MaterialTheme.typography.headlineLarge,
//            textAlign = TextAlign.Center
//        )
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
//        ) {
//            AssistChip(
//                onClick = { },
//                label = { Text("All") },
//            )
//            AssistChip(
//                onClick = { },
//                label = { Text("Personal") },
//            )
//            AssistChip(
//                onClick = { },
//                label = { Text("Transactions") },
//            )
//            AssistChip(
//                onClick = { },
//                label = { Text("OTPs") },
//            )
//        }
//        LazyColumn {
//            item { EventItem() }
//        }
//    }
}
