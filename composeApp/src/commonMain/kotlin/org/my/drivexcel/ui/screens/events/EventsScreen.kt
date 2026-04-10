package org.my.drivexcel.ui.screens.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.ui.components.AlertDialogComponent
import org.my.drivexcel.ui.screens.events.components.EventListItemComponent

@Composable
fun EventsRoute(
    viewModel: EventsViewModel = koinViewModel(),
    onEventPressedNavigate: (id: String) -> Unit,
    onCreateEventNavigate: () -> Unit,
    onUpdateEventNavigate: (id: String) -> Unit

) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is EventsUiEvent.UpdateEvent -> onUpdateEventNavigate(event.id)
                EventsUiEvent.CreateEvent -> onCreateEventNavigate()
                is EventsUiEvent.EventPressed -> onEventPressedNavigate(event.id)
            }
        }
    }

    EventsScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
    )

    if (uiState.isDeleteDialogVisible) {
        AlertDialogComponent(
            onDismissRequest = { viewModel.onAction(EventsUIAction.OnDeleteDismissClicked) },
            onConfirmation = { viewModel.onAction(EventsUIAction.DeleteEvent) },
            dialogTitle = "Удалить мероприятие",
            dialogText = "Вы действительно хотите удалить мероприятие?\nВы уже не сможете его восстановить.",
            icon = Icons.Default.Delete
        )
    }
}

@Composable
private fun EventsScreen(
    uiState: EventsUIState,
    onAction: (EventsUIAction) -> Unit,

    ) {
    EventsWrapper(
        uiState = uiState,
        onAction = onAction,
        content = { innerPadding ->
            EventsContent(
                innerPadding = innerPadding,
                uiState = uiState,
                onAction = onAction,
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventsWrapper(
    uiState: EventsUIState,
    onAction: (EventsUIAction) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Список"
                    )
                },
                actions = {
                    IconButton(onClick = {
                        onAction(EventsUIAction.CreateEvent)
                    }) {
                        Icon(
                            imageVector = Icons.Default.AddChart,
                            contentDescription = null
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onAction(EventsUIAction.OnEditingClicked)
                    }) {
                        if (!uiState.isEditingMode) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Close,
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
private fun EventsContent(
    innerPadding: PaddingValues,
    uiState: EventsUIState,
    onAction: (EventsUIAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(top = innerPadding.calculateTopPadding())
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(
            items = uiState.events,
            key = { it.id }
        ) { item ->
            EventListItemComponent(
                modifier = Modifier.animateItem(),
                eventId = item.id,
                byteArray = item.byteArray,
                eventName = item.name,
                isEditingMode = uiState.isEditingMode,
                onAction = onAction
            )
        }
    }
}