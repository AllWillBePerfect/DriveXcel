package org.my.drivexcel.v4.ui.screens.importt

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.my.drivexcel.v4.ui.components.InfoMessageComponent
import org.my.drivexcel.v4.ui.models.UiIcon
import org.my.drivexcel.v4.ui.models.UiText
import org.my.drivexcel.v4.ui.module.XlsFilePicker
import org.my.drivexcel.v4.ui.screens.importt.components.XlsListItem
import org.my.drivexcel.v4.ui.screens.importt.models.ImportUiAction
import org.my.drivexcel.v4.ui.screens.importt.models.ImportUiState

@Composable
fun ImportRoute(
    eventId: String,
    viewModel: ImportViewModel = koinViewModel(
        parameters = {
            parametersOf(eventId)
        }
    ),
    xlsFilePicker: XlsFilePicker = koinInject()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val onFileSelected = xlsFilePicker.registerPicker(viewModel::onFilesSelected)

    ImportScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onFileSelected = onFileSelected
    )
}

@Composable
private fun ImportScreen(
    uiState: ImportUiState,
    onAction: (ImportUiAction) -> Unit,
    onFileSelected: () -> Unit,
) {
    ImportWrapper(
        content = { innerPadding ->
            ImportContent(
                innerPadding = innerPadding,
                uiState = uiState,
                onAction = onAction,
                onFileSelected = onFileSelected
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImportWrapper(
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold { innerPadding ->
        content(innerPadding)
    }
}

@Composable
private fun ImportContent(
    innerPadding: PaddingValues,
    uiState: ImportUiState,
    onAction: (ImportUiAction) -> Unit,
    onFileSelected: () -> Unit
) {

    Column {
        Row(
            Modifier.padding(horizontal = 8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onFileSelected,
                shape = RoundedCornerShape(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.UploadFile,
                    contentDescription = null
                )
                Text("Импорт")
            }
            Spacer(modifier = Modifier.width(1.dp))
            Button(
                modifier = Modifier.weight(1f),
                onClick = { onAction(ImportUiAction.OnSaveXlsClicked) },
                shape = RoundedCornerShape(4.dp),
                enabled = uiState.isSaveEnabled
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null
                )
                Text("Сохранить")
            }
        }
        LazyColumn {
            items(uiState.files) { file ->
                XlsListItem(
                    fileName = file.fileName,
                    onDeleteClicked = { onAction(ImportUiAction.OnDeleteClicked(file.id)) }
                )
            }
        }
        val text = buildAnnotatedString {
            appendLine("Список пустой.")
            append("Загрузите файлы формата ")

            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("xls/xlsx")
            }

            append(" через кнопку ")

            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append("«Импорт»")
            }
        }

        if (uiState.isEmptyList) {
            Box(modifier = Modifier.fillMaxSize()) {
                InfoMessageComponent(
                    modifier = Modifier.align(Alignment.TopCenter),
                    icon = UiIcon.Vector(Icons.AutoMirrored.Default.ListAlt),
                    text = UiText.AnnotatedString(text)
                )
            }
        }
    }
}