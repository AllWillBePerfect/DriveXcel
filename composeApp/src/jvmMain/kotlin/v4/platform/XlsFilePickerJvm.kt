package v4.platform

import androidx.compose.runtime.Composable
import org.my.drivexcel.ui.platform.XlsFilePicker
import org.my.drivexcel.ui.models.PickedFile
import java.awt.FileDialog
import java.awt.Frame
import java.io.FilenameFilter

class XlsFilePickerJvm : XlsFilePicker {
    @Composable
    override fun registerPicker(onFilesSelected: (List<PickedFile>) -> Unit): () -> Unit {

        return {
            val dialog = FileDialog(null as Frame?, "Выберите xls/xlsx файл(ы)", FileDialog.LOAD)
            dialog.isMultipleMode = true
            dialog.filenameFilter = FilenameFilter { _, name ->
                name.endsWith(".xls", true) ||
                        name.endsWith(".xlsx", true)
            }

            dialog.isVisible = true

            val files = dialog.files
            val pickedFiles = files.map { file ->
                PickedFile(
                    name = file.name,
                    bytes = file.readBytes()
                )
            }
            onFilesSelected.invoke(pickedFiles)

        }
    }
}