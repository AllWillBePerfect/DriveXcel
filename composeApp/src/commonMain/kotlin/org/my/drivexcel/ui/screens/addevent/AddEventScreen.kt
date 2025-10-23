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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_arrow_back
import drivexcel.composeapp.generated.resources.ic_close
import drivexcel.composeapp.generated.resources.ic_create_folder
import drivexcel.composeapp.generated.resources.ic_image_upload
import drivexcel.composeapp.generated.resources.preview
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.platform.utils.BackHandlerProvider
import org.my.drivexcel.platform.utils.ImagePicker
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
    onBackPressed: () -> Unit
) {

    backHandlerProvider.BackHandler {
        onBackPressed()
    }

    val eventName by viewModel.eventName.collectAsState()
    val isButtonEnabled by viewModel.isButtonEnabled.collectAsState()
    val image by viewModel.image.collectAsState()

    imagePicker.PickImage(viewModel::setImage)



    AddEventScreen(
        eventName = eventName,
        image = image,
        isButtonEnabled = isButtonEnabled,
        changeEventName = viewModel::changeEventName,
        launchPicker = imagePicker::launchPicker,
        removeImage = viewModel::removeImage,
        createEventDirectory = viewModel::createEventDirectory,
        onBackPressed = onBackPressed
    )
}

@Composable
private fun AddEventScreen(
    eventName: String,
    image: ImageBitmap?,
    isButtonEnabled: Boolean,
    changeEventName: (String) -> Unit,
    launchPicker: () -> Unit,
    removeImage: () -> Unit,
    createEventDirectory: () -> Unit,
    onBackPressed: () -> Unit
) {

    val windowSize = LocalWindowSize.current
    val isDesktopSize = windowSize.isWide


    val content = addEventLazyColumnContent(
        eventName = eventName,
        image = image,
        isButtonEnabled = isButtonEnabled,
        changeEventName = changeEventName,
        launchPicker = launchPicker,
        removeImage = removeImage,
        createEventDirectory = createEventDirectory

    )

    AddEventContainer(
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
    onBackPressed: () -> Unit,
    content: @Composable (
        innerPadding: PaddingValues,
    ) -> Unit
) {

    val windowSize = LocalWindowSize.current
    val isWide = windowSize.isWide

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (!isWide) {
                        Text(
                            text = "Добавить мероприятие"
                        )
                    }
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
    eventName: String,
    image: ImageBitmap?,
    isButtonEnabled: Boolean,
    changeEventName: (String) -> Unit,
    launchPicker: () -> Unit,
    removeImage: () -> Unit,
    createEventDirectory: () -> Unit
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
            launchPicker = launchPicker,
            removeImage = removeImage
        )
    }
    item {
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = isButtonEnabled,
            onClick = createEventDirectory
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_create_folder),
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text(text = "Добавить мероприятие")
        }
    }
}

@Composable
private fun ImageView(
    image: ImageBitmap?,
    launchPicker: () -> Unit,
    removeImage: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        onClick = { if (image == null) launchPicker() }
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

        eventName = "Привет!",
        image = imageResource(Res.drawable.preview),
        isButtonEnabled = true,
        changeEventName = {},
        launchPicker = {},
        removeImage = {},
        createEventDirectory = {},
        onBackPressed = {}
    )

}

@PhonePreview
@Composable
private fun AddEventScreenPreviewLight() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = phoneWindowSizeClassPreview
) {

    Res.drawable.preview

    AddEventScreen(

        image = imageResource(Res.drawable.preview),
        eventName = "Привет!",
        isButtonEnabled = true,
        changeEventName = {},
        launchPicker = {},
        removeImage = {},
        createEventDirectory = {},
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

        eventName = "Привет!",
        image = imageResource(Res.drawable.preview),
        isButtonEnabled = true,
        changeEventName = {},
        launchPicker = {},
        removeImage = {},
        createEventDirectory = {},
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

        eventName = "Привет!",
        image = imageResource(Res.drawable.preview),
        isButtonEnabled = true,
        changeEventName = {},
        launchPicker = {},
        removeImage = {},
        createEventDirectory = {},
        onBackPressed = {}
    )

}