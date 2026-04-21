package org.my.drivexcel.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.platform.desktopWindowSizeClassPreview
import org.my.drivexcel.platform.phoneWindowSizeClassPreview
import org.my.drivexcel.ui.components.CenteredContainerComponent
import org.my.drivexcel.ui.screens.login.model.LoginUiAction
import org.my.drivexcel.ui.screens.login.model.LoginUiEvent
import org.my.drivexcel.ui.screens.login.model.LoginUiState
import org.my.drivexcel.ui.theme.DesktopPreview
import org.my.drivexcel.ui.theme.DriveXcelAppTheme
import org.my.drivexcel.ui.theme.PhonePreview
import org.my.drivexcel.ui.utils.nav.desktopWindowSizeClassPreviewProvider
import org.my.drivexcel.ui.utils.nav.phoneWindowSizeClassPreviewProvider

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
    val items = listOf(0, 1, 2, 3, 4)
    val visibleStates = remember { items.map { mutableStateOf(false) } }

    LaunchedEffect(Unit) {
        visibleStates.forEachIndexed { index, state ->
            delay(index * 200L)
            state.value = true
        }
    }

    CenteredContainerComponent {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(32.dp))

            AnimatedVisibility(
                visible = visibleStates[0].value,
                enter = anim()
            ) {
                Text(
                    text = "Добро пожаловать",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(
                visible = visibleStates[1].value,
                enter = anim()
            ) {
                Text(
                    text = "Приложение помогает быстро работать со списками участников мероприятий.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(
                visible = visibleStates[2].value,
                enter = anim()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureItem("Объединяет Excel-файлы")
                    FeatureItem("Убирает дубликаты")
                    FeatureItem("Упрощает поиск по ФИО")
                    FeatureItem("Быстро копирует ID")
                }
            }

            Spacer(Modifier.height(32.dp))

            AnimatedVisibility(
                visible = visibleStates[3].value,
                enter = anim()
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    enabled = !uiState.clickPerformed,
                    onClick = { onAction(LoginUiAction.AuthorizeUser) }
                ) {
                    if (!uiState.clickPerformed) {
                        Text("Начать")
                    } else {
                        CircularProgressIndicator(strokeWidth = 2.dp)
                    }
                }
            }

            Spacer(Modifier.height(48.dp))

            AnimatedVisibility(
                visible = visibleStates[4].value,
                enter = anim()


            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Или",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center

                    )
                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                        ,
                        enabled = !uiState.clickPerformed,
                        onClick = { onAction(LoginUiAction.AuthorizeTestUser) }
                    ) {
                        if (!uiState.clickPerformed) {
                            Text("Использовать тестовые данные")
                        } else {
                            CircularProgressIndicator(strokeWidth = 2.dp)
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun FeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("• ", style = MaterialTheme.typography.bodyMedium)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun anim()  = fadeIn(
    animationSpec = tween(durationMillis = 700)
) + slideInHorizontally(
    animationSpec = tween(durationMillis = 700),
    initialOffsetX = { -it }
)

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
    windowSizeClassProvider = phoneWindowSizeClassPreviewProvider
) {
    DefaultPreviewItem()
}

@PhonePreview
@Composable
private fun PreviewLight() =
    DriveXcelAppTheme(
        darkTheme = false,
        myWindowSizeClass = phoneWindowSizeClassPreview,
        windowSizeClassProvider = phoneWindowSizeClassPreviewProvider
    ) {
        DefaultPreviewItem()
    }

@DesktopPreview
@Composable
private fun PreviewNightDesktop() = DriveXcelAppTheme(
    darkTheme = true,
    myWindowSizeClass = desktopWindowSizeClassPreview,
    windowSizeClassProvider = desktopWindowSizeClassPreviewProvider
) {
    DefaultPreviewItem()
}

@DesktopPreview
@Composable
private fun PreviewLightDesktop() = DriveXcelAppTheme(
    darkTheme = false,
    myWindowSizeClass = desktopWindowSizeClassPreview,
    windowSizeClassProvider = desktopWindowSizeClassPreviewProvider
) {
    DefaultPreviewItem()
}
