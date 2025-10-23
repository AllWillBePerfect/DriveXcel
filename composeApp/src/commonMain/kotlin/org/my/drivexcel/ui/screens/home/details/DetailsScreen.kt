package org.my.drivexcel.ui.screens.home.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_arrow_back
import org.jetbrains.compose.resources.painterResource
import org.my.drivexcel.ui.screens.home.HomeViewModel


@Composable
fun DetailsScreen(
    uiState: HomeViewModel.HomeUiState,
    onEventClose: () -> Unit
) {

    DetailsScreenContainer(
        uiState = uiState,
        onEventClose = onEventClose,
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
    onEventClose: () -> Unit,
    content: @Composable (
        uiState: HomeViewModel.HomeUiState.Loaded,
        innerPadding: PaddingValues
    ) -> Unit
) {

    val appBarText = when (uiState) {
        is HomeViewModel.HomeUiState.Loaded ->
            when (uiState.selectedEvent) {
                is HomeViewModel.EventOnDetailsUiState.EventSelected -> uiState.selectedEvent.eventId
                HomeViewModel.EventOnDetailsUiState.NoEventSelected -> "None"
            }

        else -> "Loading"
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

        when (uiState) {
            is HomeViewModel.HomeUiState.Loaded -> content(
                uiState,
                innerPadding
            )
            HomeViewModel.HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.White)
                )
            }
        }
    }
}

@Composable
private fun DetailsScreenContent(
    uiState: HomeViewModel.HomeUiState.Loaded,
    innerPadding: PaddingValues
) {


    when (uiState.selectedEvent) {
        is HomeViewModel.EventOnDetailsUiState.EventSelected -> {

            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.selectedEvent.users) {
                    DetailsUserItem(
                        leaderUser = it
                    )
                }
            }

        }

        HomeViewModel.EventOnDetailsUiState.NoEventSelected -> {

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