package org.my.drivexcel.platform.utils

import androidx.compose.runtime.Composable
import java.awt.EventQueue
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


class ImagePickerJvm : ImagePicker {
    private var onImageSelected: ((ByteArray) -> Unit)? = null

    @Composable
    override fun RegisterImagePicker(onImageSelected: (ByteArray) -> Unit) {
        this.onImageSelected = onImageSelected
    }

    /**
     * EventQueue.invokeLater для возможно запуска в корутине
     */
    override fun launchPicker() = EventQueue.invokeLater {
        awtVersion()
    }

    private fun awtVersion() {
        val dialog = FileDialog(null as Frame?, "Выберите изображение", FileDialog.LOAD)

        dialog.filenameFilter = FilenameFilter { _, name ->
            name.endsWith(".png", true) ||
                    name.endsWith(".jpg", true) ||
                    name.endsWith(".jpeg", true) ||
                    name.endsWith(".webp", true) ||
                    name.endsWith(".bmp", true)
        }

        dialog.isVisible = true

        dialog.files.firstOrNull()?.let { file ->
            val bytes = file.readBytes()
            onImageSelected?.invoke(bytes)
        }
    }

    private fun swingVersion() {
        val chooser = JFileChooser()

        chooser.fileFilter = FileNameExtensionFilter(
            "Images",
            "png", "jpg", "jpeg", "webp", "bmp"
        )

//        chooser.acceptAllFileFilterUsed = false
        chooser.currentDirectory = File(System.getProperty("user.home"), "Pictures")

        val result = chooser.showOpenDialog(null)

        if (result == JFileChooser.APPROVE_OPTION) {
            val bytes = chooser.selectedFile.readBytes()
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