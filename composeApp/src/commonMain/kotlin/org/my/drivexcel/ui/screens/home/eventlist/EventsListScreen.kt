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
import org.my.drivexcel.ui.screens.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsListScreen(
    uiState: HomeViewModel.HomeUiState,
    showAppBar: Boolean,
    onEventClick: (String) -> Unit,
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
                /*Box(
                    modifier = Modifier.fillMaxSize().background(Color.White)
                )*/

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
    @Suppress("ModifierParameter") contentModifier: Modifier,
    innerPadding: PaddingValues,
    onEventClick: (String) -> Unit
) {
    LazyColumn(
//                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
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

        itemsIndexed(items = uiState.eventsHome) { index, item ->
            EventListItem(
                name = "[$index] Директория: ${item.id}",
                image = item.image,
                onEventClick = { onEventClick(item.id) }
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