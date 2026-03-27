package org.my.drivexcel.v4.ui.screens.users

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.my.drivexcel.v4.ui.components.InfoMessageComponent
import org.my.drivexcel.v4.ui.screens.event.components.LeaderUserListItemComponent
import org.my.drivexcel.v4.ui.models.UiIcon
import org.my.drivexcel.v4.ui.models.UiText

@Composable
fun UsersRoute(
    eventId: String,
    viewModel: UsersViewModel = koinViewModel(
        parameters = {
            parametersOf(eventId)
        }
    )
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UsersScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun UsersScreen(
    uiState: UsersUiState,
    onAction: (UsersUiAction) -> Unit
) {
    UsersWrapper(
        content = { innerPadding ->
            UsersContent(
                innerPadding = innerPadding,
                uiState = uiState,
                onAction = onAction
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UsersWrapper(
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = {
//                    Text(
//                        text = "Users"
//                    )
//                }
//            )
//        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
private fun UsersContent(
    innerPadding: PaddingValues,
    uiState: UsersUiState,
    onAction: (UsersUiAction) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        if (!uiState.initialLoading) {
            Column {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.userInput,
                    onValueChange = { onAction(UsersUiAction.OnTextTyped(it)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    label = { Text("Поиск") }
                )

                if (uiState.users.isNotEmpty()) {
                    LazyColumn {
                        items(uiState.users) {
                            LeaderUserListItemComponent(
                                userId = it.id,
                                userName = it.fullName
                            )
                        }
                    }
                }

                if (uiState.isEmptyList) {
                    InfoMessageComponent(
                        modifier = Modifier,
                        icon = UiIcon.Vector(Icons.AutoMirrored.Filled.ListAlt),
                        text = UiText.Text("Нет пользователей.\nЗагрузите список на вкладке \"Импорт\"")
                    )
                }
            }
        } else {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}