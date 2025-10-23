package org.my.drivexcel.ui.screens.addevent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_arrow_back
import drivexcel.composeapp.generated.resources.ic_close
import drivexcel.composeapp.generated.resources.ic_create_folder
import drivexcel.composeapp.generated.resources.ic_image_upload
import drivexcel.composeapp.generated.resources.preview
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.platform.utils.BackHandlerProvider
import org.my.drivexcel.platform.utils.ImagePicker
import org.my.drivexcel.platform.utils.WindowSize
import org.my.drivexcel.platform.utils.desktopWindowSizeClassPreview
import org.my.drivexcel.platform.utils.isWide
import org.my.drivexcel.platform.utils.phoneWindowSizeClassPreview
import org.my.drivexcel.theme.DesktopPreview
import org.my.drivexcel.theme.DriveXcelAppTheme
import org.my.drivexcel.theme.LocalWindowSize
import org.my.drivexcel.theme.PhonePreview

@Composable
fun AddEventRoute(
    viewModel: AddEventViewModel = koinViewModel(),
    imagePicker: ImagePicker = koinInject(),
    backHandlerProvider: BackHandlerProvider = koinInject(),
    format: AddEventScreenFormat = AddEventScreenFormat.AddEvent,
    onBackPressed: () -> Unit
) {

    backHandlerProvider.BackHandler {
        onBackPressed()
    }

    LaunchedEffect(format) {
        if (format is AddEventScreenFormat.RedactingEvent) {
            viewModel.loadSavedEventData(format.id)
        }
        if (format == AddEventScreenFormat.AddEvent) {
            viewModel.cleanPendingEvent()
        }
    }

    val eventName by viewModel.eventName.collectAsState()
    val isButtonEnabled by viewModel.isButtonEnabled.collectAsState()
    val image by viewModel.image.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    imagePicker.RegisterImagePicker(viewModel::setImage)

    val windowSize = LocalWindowSize.current

    AddEventScreen(
        windowSize = windowSize,
        format = format,
        eventName = eventName,
        image = image,
        isButtonEnabled = isButtonEnabled,
        isLoading = isLoading,
        changeEventName = viewModel::changeEventName,
        launchPicker = imagePicker::launchPicker,
        removeImage = viewModel::removeImage,
        createEventDirectory = viewModel::createEventDirectory,
        redactingEventDirectory = viewModel::redactingEventDirectory,
        onBackPressed = onBackPressed
    )
}

@Composable
private fun AddEventScreen(
    windowSize: WindowSize,
    format: AddEventScreenFormat,
    eventName: String,
    image: ImageBitmap?,
    isLoading: Boolean,
    isButtonEnabled: Boolean,
    changeEventName: (String) -> Unit,
    launchPicker: () -> Unit,
    removeImage: () -> Unit,
    createEventDirectory: () -> Unit,
    redactingEventDirectory: (String) -> Unit,
    onBackPressed: () -> Unit
) {

    val isDesktopSize = windowSize.isWide

    val content = addEventLazyColumnContent(
        format = format,
        eventName = eventName,
        image = image,
        isLoading = isLoading,
        isButtonEnabled = isButtonEnabled,
        changeEventName = changeEventName,
        launchPicker = launchPicker,
        removeImage = removeImage,
        createEventDirectory = createEventDirectory,
        redactingEventDirectory = redactingEventDirectory

    )

    AddEventContainer(
        windowSize = windowSize,
        format = format,
        onBackPressed = onBackPressed,
        content = { innerPadding ->
//            if (isDesktopSize) {
//                ExpandableAddEventContent(
//                    innerPadding = innerPadding,
//                    content = content
//                )
//            } else {
//                CompactAddEventContent(
//                    innerPadding = innerPadding,
//                    content = content
//                )
//            }

            AddEventContentLayout(
                innerPadding = innerPadding,
                isDesktop = isDesktopSize,
                content = content
            )

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEventContainer(
    windowSize: WindowSize,
    format: AddEventScreenFormat,
    onBackPressed: () -> Unit,
    content: @Composable (
        innerPadding: PaddingValues,
    ) -> Unit
) {

    val isWide = windowSize.isWide

    val appBarTitle = when (format) {
        AddEventScreenFormat.AddEvent -> "Add Event"
        is AddEventScreenFormat.RedactingEvent -> "Redacting Event"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = appBarTitle
                    )
                },
                navigationIcon = {
                    if (!isWide) {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_arrow_back),
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
private fun CompactAddEventContent(
    innerPadding: PaddingValues,
    content: LazyListScope.() -> Unit

) {

    Column(
        modifier = Modifier
            .padding(innerPadding)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun ExpandableAddEventContent(
    innerPadding: PaddingValues,
    content: LazyListScope.() -> Unit

) {

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        LazyColumn(
            modifier = Modifier
                .width(400.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun AddEventContentLayout(
    innerPadding: PaddingValues,
    isDesktop: Boolean,
    content: LazyListScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .then(
                if (isDesktop) Modifier.fillMaxWidth() else Modifier
            ),
        horizontalAlignment = if (isDesktop) Alignment.CenterHorizontally else Alignment.Start
    ) {
        LazyColumn(
            modifier = if (isDesktop) Modifier.width(400.dp) else Modifier,
            contentPadding = PaddingValues(horizontal = 16.dp),
            content = content
        )
    }
}

@Composable
private fun addEventLazyColumnContent(
    format: AddEventScreenFormat,
    eventName: String,
    image: ImageBitmap?,
    isLoading: Boolean,
    isButtonEnabled: Boolean,
    changeEventName: (String) -> Unit,
    launchPicker: () -> Unit,
    removeImage: () -> Unit,
    createEventDirectory: () -> Unit,
    redactingEventDirectory: (String) -> Unit
): LazyListScope.() -> Unit = {

    item {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "Название директории",
        )
    }
    item {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = eventName,
            onValueChange = changeEventName,
            enabled = !isLoading,
            placeholder = {
                Text(
                    text = "Введите название мероприятия*"
                )
            }
        )
    }
    item {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "Изображение мероприятия",
        )
    }
    item {
        ImageView(
            image = image,
            enabled = !isLoading,
            launchPicker = launchPicker,
            removeImage = removeImage
        )
    }

    item {
        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .clip(RoundedCornerShape(90.dp)),
            thickness = 2.dp
        )
    }

    item {
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = isButtonEnabled,
            onClick = {
                when (format) {
                    AddEventScreenFormat.AddEvent -> {
                        createEventDirectory()
                    }

                    is AddEventScreenFormat.RedactingEvent -> {
                        redactingEventDirectory(format.id)
                    }
                }
            },
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Icon(
                    painter = painterResource(Res.drawable.ic_create_folder),
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = when (format) {
                        AddEventScreenFormat.AddEvent -> "Добавить мероприятие"
                        is AddEventScreenFormat.RedactingEvent -> "Редактировать мероприятие"
                    }
                )
            }
        }
    }
}

@Composable
private fun ImageView(
    image: ImageBitmap?,
    enabled: Boolean,
    launchPicker: () -> Unit,
    removeImage: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        onClick = { if (image == null) launchPicker() },
        enabled = enabled
    ) {
        Box(Modifier.fillMaxSize()) {
            if (image == null) {
                Icon(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    painter = painterResource(Res.drawable.ic_image_upload),
                    contentDescription = null
                )
            } else {
                Image(
                    bitmap = image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                    ),
                    onClick = removeImage
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_close),
                        contentDescription = "Удалить изображение"
                    )
                }
            }
        }
    }
}


@PhonePreview
@Composable
private fun AddEventScreenPreviewNight() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = phoneWindowSizeClassPreview
) {


    AddEventScreen(
        windowSize = WindowSize.Compact,
        format = AddEventScreenFormat.AddEvent,
        eventName = "Привет!",
        image = imageResource(Res.drawable.preview),
        isLoading = false,
        isButtonEnabled = true,
        changeEventName = {},
        launchPicker = {},
        removeImage = {},
        createEventDirectory = {},
        redactingEventDirectory = {},
        onBackPressed = {}
    )

}

@PhonePreview
@Composable
private fun AddEventScreenPreviewLight() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = phoneWindowSizeClassPreview
) {

    AddEventScreen(
        windowSize = WindowSize.Compact,
        format = AddEventScreenFormat.AddEvent,
        image = imageResource(Res.drawable.preview),
        eventName = "Привет!",
        isButtonEnabled = true,
        isLoading = false,
        changeEventName = {},
        launchPicker = {},
        removeImage = {},
        createEventDirectory = {},
        redactingEventDirectory = {},
        onBackPressed = {}
    )

}

@DesktopPreview
@Composable
private fun AddEventScreenPreviewNightDesktop() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = desktopWindowSizeClassPreview
) {

    AddEventScreen(
        windowSize = WindowSize.Expanded,
        format = AddEventScreenFormat.AddEvent,
        eventName = "Привет!",
        image = imageResource(Res.drawable.preview),
        isButtonEnabled = true,
        isLoading = false,
        changeEventName = {},
        launchPicker = {},
        removeImage = {},
        createEventDirectory = {},
        redactingEventDirectory = {},
        onBackPressed = {}
    )

}

@DesktopPreview
@Composable
private fun AddEventScreenPreviewLightDesktop() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = desktopWindowSizeClassPreview
) {

    AddEventScreen(
        windowSize = WindowSize.Expanded,
        format = AddEventScreenFormat.AddEvent,
        eventName = "Привет!",
        image = imageResource(Res.drawable.preview),
        isButtonEnabled = true,
        isLoading = false,
        changeEventName = {},
        launchPicker = {},
        removeImage = {},
        createEventDirectory = {},
        redactingEventDirectory = {},
        onBackPressed = {}
    )

}

sealed interface AddEventScreenFormat {
    data object AddEvent : AddEventScreenFormat
    data class RedactingEvent(
        val id: String,
    ) : AddEventScreenFormat
}
