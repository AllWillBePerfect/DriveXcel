package org.my.drivexcel.ui.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.platform.phoneWindowSizeClassPreview
import org.my.drivexcel.ui.theme.DriveXcelAppTheme
import org.my.drivexcel.ui.theme.PhonePreview
import org.my.drivexcel.ui.components.CenteredContainerComponent
import org.my.drivexcel.ui.screens.login.model.LoginUiAction
import org.my.drivexcel.ui.screens.login.model.LoginUiEvent
import org.my.drivexcel.ui.screens.login.model.LoginUiState
import org.my.drivexcel.ui.utils.nav.windowSizeClassPreviewProvider

@Composable
fun LoginRoute(
    viewModel: LoginViewModel = koinViewModel(),
    onClickButtonNavigate: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                LoginUiEvent.NavigateToMainGraph -> onClickButtonNavigate()
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun LoginScreen(
    uiState: LoginUiState,
    onAction: (LoginUiAction) -> Unit
) {
    LoginWrapper(
        content = { innerPadding ->
            LoginContent(
                innerPadding = innerPadding,
                uiState = uiState,
                onAction = onAction
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginWrapper(
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {

                }
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
private fun LoginContent(
    innerPadding: PaddingValues,
    uiState: LoginUiState,
    onAction: (LoginUiAction) -> Unit
) {

    CenteredContainerComponent {
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            ElevatedButton(
                enabled = !uiState.clickPerformed,
                onClick = { onAction(LoginUiAction.AuthorizeUser) }
            ) {
                if (!uiState.clickPerformed) {
                    Text("использовать локальное хранилище")
                } else {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun DefaultPreviewItem() = LoginScreen(
    uiState = LoginUiState(),
    onAction = {}
)

@PhonePreview
@Composable
private fun PreviewNight() = DriveXcelAppTheme(
    darkTheme = true,
    myWindowSizeClass = phoneWindowSizeClassPreview,
    windowSizeClassProvider = windowSizeClassPreviewProvider
) {
    DefaultPreviewItem()
}

@PhonePreview
@Composable
private fun PreviewLight() =
    DriveXcelAppTheme(
        darkTheme = false,
        myWindowSizeClass = phoneWindowSizeClassPreview,
        windowSizeClassProvider = windowSizeClassPreviewProvider
    ) {
        DefaultPreviewItem()
    }