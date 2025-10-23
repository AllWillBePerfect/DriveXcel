package org.my.drivexcel.ui.screens.home.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_arrow_back
import org.jetbrains.compose.resources.painterResource
import org.my.drivexcel.ui.screens.home.DetailsSubScreens
import org.my.drivexcel.ui.screens.home.HomeViewModel
import org.my.drivexcel.ui.screens.home.details.subcontainers.MergeSubContainer


@Composable
fun DetailsScreen(
    uiState: HomeViewModel.HomeUiState,
    detailsTabsNavController: NavHostController,
    switchTabOnDetails: (Int) -> Unit,
    onEventClose: () -> Unit,
) {

    DetailsScreenContainer(
        uiState = uiState,
        detailsTabsNavController = detailsTabsNavController,
        onEventClose = onEventClose,
        switchTabOnDetails = switchTabOnDetails,
        content = { uiState, innerPadding ->
            DetailsScreenContent(
                uiState = uiState,
                innerPadding = innerPadding
            )
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsScreenContainer(
    uiState: HomeViewModel.HomeUiState,
    detailsTabsNavController: NavHostController,
    onEventClose: () -> Unit,
    switchTabOnDetails: (Int) -> Unit,
    content: @Composable (
        uiState: HomeViewModel.HomeUiState.Loaded,
        innerPadding: PaddingValues
    ) -> Unit
) {


//    var selectedDestination by rememberSaveable { mutableIntStateOf(DetailsSubScreens.Users.ordinal) }
    val selectedDestination = when (uiState) {
        is HomeViewModel.HomeUiState.Loaded -> uiState.selectedEvent.selectedTabOnDetails
        HomeViewModel.HomeUiState.Loading -> 0
    }

    val appBarText =
        when (uiState) {
            is HomeViewModel.HomeUiState.Loaded -> {
                if (uiState.selectedEvent.isLoading) {
                    "Загрузка..."
                } else {
                    when (uiState.selectedEvent) {
                        is HomeViewModel.EventOnDetailsUiState.EventSelected -> uiState.selectedEvent.eventName
                        is HomeViewModel.EventOnDetailsUiState.NoEventSelected -> "None"
                    }
                }
            }

            else -> "None"
        }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(appBarText) },
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
        }
    ) { innerPadding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            PrimaryTabRow(
                selectedTabIndex = selectedDestination,
            ) {
                DetailsSubScreens.entries.forEachIndexed { index, screens ->
                    Tab(
                        selected = selectedDestination == index,
                        onClick = {
                            detailsTabsNavController.navigate(route = screens.route)
                            switchTabOnDetails(index)
                        },
                        text = { Text(screens.route) }
                    )
                }
            }

            // Content должен учитывать высоту TabRow → просто Modifier.weight(1f)
            NavHost(
                navController = detailsTabsNavController,
                startDestination = DetailsSubScreens.Users.route,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                composable(DetailsSubScreens.Users.route) {
                    if (uiState is HomeViewModel.HomeUiState.Loaded) {
                        content(uiState, innerPadding)
                    }
                }

                composable(DetailsSubScreens.Merge.route) {
//                    CenterText(DetailsSubScreens.Merge.route)
                    MergeSubContainer()
                }

                composable(DetailsSubScreens.Data.route) {
                    CenterText(DetailsSubScreens.Data.route)
                }
            }
        }


    }
}

@Composable
private fun CenterText(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text)
    }
}

@Composable
private fun DetailsScreenContent(
    uiState: HomeViewModel.HomeUiState.Loaded,
    innerPadding: PaddingValues
) {


    when (uiState.selectedEvent) {
        is HomeViewModel.EventOnDetailsUiState.EventSelected -> {

            if (!uiState.selectedEvent.isLoading) {
                if (uiState.selectedEvent.users.isNotEmpty()) {
                    LazyColumn(
                        contentPadding = PaddingValues(
//                            top = innerPadding.calculateTopPadding(),
                            bottom = innerPadding.calculateBottomPadding(),
//                            start = 16.dp,
//                            end = 16.dp
                        ),
//                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(uiState.selectedEvent.users) {
                            DetailsUserItem(
                                leaderUser = it
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),

                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Text("Список пустой. Добавьте файл с пользователями.")
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

        }

        is HomeViewModel.EventOnDetailsUiState.NoEventSelected -> {

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


