package org.my.drivexcel.ui.platform

import androidx.compose.runtime.Composable
import org.my.drivexcel.ui.models.PickedFile

interface XlsFilePicker {

    @Composable
    fun registerPicker(onFilesSelected: (List<PickedFile>) -> Unit): () -> Unit

}