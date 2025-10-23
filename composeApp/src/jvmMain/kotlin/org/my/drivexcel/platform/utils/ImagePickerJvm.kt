package org.my.drivexcel.platform.utils

import androidx.compose.runtime.Composable
import java.awt.FileDialog
import java.awt.Frame


class ImagePickerJvm : ImagePicker {
    private var onImageSelected: ((ByteArray) -> Unit)? = null

    @Composable
    override fun RegisterImagePicker(onImageSelected: (ByteArray) -> Unit) {
        this.onImageSelected = onImageSelected
    }

    override fun launchPicker() {
        val dialog = FileDialog(null as Frame?, "Выберите изображение", FileDialog.LOAD)
        dialog.isVisible = true
        dialog.files.firstOrNull()?.let { file ->
            val bytes = file.readBytes()
            onImageSelected?.invoke(bytes)
        }
    }
}

//class ImagePickerJvm : ImagePicker {
//    @Composable
//    override fun PickImage(onImageSelected: (ByteArray) -> Unit) {
//        var showDialog by remember { mutableStateOf(false) }
//
//        LaunchedEffect(showDialog) {
//            if (showDialog) {
//                val dialog = FileDialog(null as Frame?, "Выберите изображение", FileDialog.LOAD)
//                dialog.isVisible = true
//                dialog.files.firstOrNull()?.let { file ->
//                    val bytes = file.readBytes()
//                    onImageSelected(bytes)
//                }
//                showDialog = false
//            }
//        }
//
//        androidx.compose.material3.Button(onClick = { showDialog = true }) {
//            androidx.compose.material3.Text("Выбрать изображение")
//        }
//    }
//}