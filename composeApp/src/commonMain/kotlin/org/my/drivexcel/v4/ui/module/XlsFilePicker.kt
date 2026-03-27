package org.my.drivexcel.v4.ui.module

import androidx.compose.runtime.Composable
import org.my.drivexcel.v4.ui.models.PickedFile

interface XlsFilePicker {

    @Composable
    fun registerPicker(onFilesSelected: (List<PickedFile>) -> Unit): () -> Unit

}