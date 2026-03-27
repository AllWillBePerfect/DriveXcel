package org.my.drivexcel.v4.ui.screens.event_editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_arrow_back
import drivexcel.composeapp.generated.resources.ic_error
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.platform.utils.ImagePicker
import org.my.drivexcel.platform.utils.desktopWindowSizeClassPreview
import org.my.drivexcel.platform.utils.isWide
import org.my.drivexcel.platform.utils.phoneWindowSizeClassPreview
import org.my.drivexcel.theme.DesktopPreview
import org.my.drivexcel.theme.DriveXcelAppTheme
import org.my.drivexcel.theme.LocalWindowSize
import org.my.drivexcel.theme.PhonePreview
import org.my.drivexcel.v4.ui.components.InfoMessageComponent
import org.my.drivexcel.v4.ui.models.UiIcon
import org.my.drivexcel.v4.ui.models.UiText
import org.my.drivexcel.v4.ui.screens.event_editor.components.EventEditorImageComponent

@Composable
fun EventEditorRoute(
    viewModel: EventEditorViewModel = koinViewModel(),
    imagePicker: ImagePicker = koinInject(),
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    LaunchedEffect(viewModel) {
        launch {
            viewModel.events.collect { event ->
                when (event) {
                    EventEditorEvent.NavigateBack -> onBackPressed()
                    EventEditorEvent.LaunchImagePicker -> imagePicker.launchPicker()
                }
            }
        }


    }


    val onImageSelected by rememberUpdatedState { bytes: ByteArray ->
        viewModel.onAction(EventEditingAction.ImagePicked(ImageBytes(bytes)))
    }
    imagePicker.RegisterImagePicker(onImageSelected)

    EventEditorScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun EventEditorScreen(
    uiState: EventEditorUiState,
    onAction: (EventEditingAction) -> Unit,
) {
    EventEditorWrapper(
        uiState = uiState,
        onAction = onAction,
        content = { innerPadding ->
            EventEditorAdaptiveContentWrapper(
                innerPadding = innerPadding,
                content = {
                    EventEditorContent(
                        uiState = uiState,
                        onAction = onAction
                    )
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventEditorWrapper(
    uiState: EventEditorUiState,
    onAction: (EventEditingAction) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (uiState.isGetEventLoadError) "Ошибка" else uiState.eventEditorMode.title
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(EventEditingAction.BackClicked) }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                },

                )
        },

    ) { innerPadding ->
        when {
            uiState.isGetEventLoadError -> {
                InfoMessageComponent(
                    modifier = Modifier.padding(innerPadding),
                    icon = UiIcon.Drawable(Res.drawable.ic_error),
                    text = UiText.Text("Произошла ошибка при загрузке мероприятия")
                )
            }

            uiState.isInitialLoading -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            else -> {
                content(innerPadding)
            }
        }
    }
}


@Composable
private fun EventEditorAdaptiveContentWrapper(
    innerPadding: PaddingValues,
    content: @Composable ColumnScope.() -> Unit
) {


    val windowSize = LocalWindowSize.current
    val isWide = windowSize.isWide

    Box(
        modifier = Modifier
            .padding(innerPadding)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .fillMaxSize(),
        contentAlignment = if (isWide) Alignment.TopCenter else Alignment.TopStart,

        ) {

        val contentModifier =
            (if (isWide) Modifier.width(400.dp)
            else Modifier.fillMaxWidth()).padding(horizontal = 16.dp)

        Column(
            modifier = contentModifier,
            content = content
        )
    }
}

@Composable
private fun EventEditorContent(
    uiState: EventEditorUiState,
    onAction: (EventEditingAction) -> Unit
) {
    Text(
        modifier = Modifier
            .testTag("event_name"),
        text = "Название мероприятия"
    )
    TextField(
        uiState.userInput,
        { onAction(EventEditingAction.InputChanged(it)) },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_input"),
        isError = !uiState.textFieldError.isNullOrBlank(),
        supportingText = { Text(uiState.textFieldError ?: "") }
    )
    Text(
        modifier = Modifier
            .testTag("banner"),
        text = "Баннер"
    )
    EventEditorImageComponent(onAction, uiState)

    /*if (uiState.isRemoveImageButtonEnabled) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onAction(EventEditingAction.ImageRemoved) }
        ) {
            Text(
                text = "Убрать изображение"
            )
        }
    }*/

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onAction(EventEditingAction.CreateEvent) },
        enabled = uiState.isButtonEnable,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = uiState.eventEditorMode.title
        )
    }

    if (uiState.eventEditorMode == EventEditorMode.UPDATE) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onAction(EventEditingAction.DeleteEvent) },
            enabled = uiState.isButtonEnable,
            shape = RoundedCornerShape(4.dp)

        ) {
            Text(
                text = "Удалить мероприятие"
            )
        }
    }

}


@Composable
private fun CreateDefaultPreview() = EventEditorScreen(
    uiState = EventEditorUiState(),
    onAction = {},
)

@PhonePreview
@Composable
private fun EventEditorPreviewNightPhone() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = phoneWindowSizeClassPreview
) {
    CreateDefaultPreview()
}

@PhonePreview
@Composable
private fun EventEditorPreviewLightPhone() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = phoneWindowSizeClassPreview
) {
    CreateDefaultPreview()
}

@DesktopPreview
@Composable
private fun EventEditorPreviewNightDesktop() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = desktopWindowSizeClassPreview
) {
    CreateDefaultPreview()
}

@DesktopPreview
@Composable
private fun EventEditorPreviewLightDesktop() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = desktopWindowSizeClassPreview
) {
    CreateDefaultPreview()
}



