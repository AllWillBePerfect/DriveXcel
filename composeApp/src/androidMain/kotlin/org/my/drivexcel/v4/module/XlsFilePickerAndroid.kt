package org.my.drivexcel.v4.module

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.my.drivexcel.v4.ui.module.XlsFilePicker
import org.my.drivexcel.v4.ui.models.PickedFile

class XlsFilePickerAndroid : XlsFilePicker {

    @Composable
    override fun registerPicker(onFilesSelected: (List<PickedFile>) -> Unit): () -> Unit {

        val ctx = LocalContext.current

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenMultipleDocuments()
        ) { uris ->

            val pickedFiles = uris.mapNotNull { uri ->
                try {
                    val name = getFileName(uri, ctx)

                    val bytes = ctx.contentResolver
                        .openInputStream(uri)
                        ?.use { it.readBytes() }

                    bytes?.let {
                        PickedFile(
                            name = name,
                            bytes = it
                        )
                    }
                } catch (e: Exception) {
                    null
                }
            }

            if (pickedFiles.isNotEmpty()) {
                onFilesSelected(pickedFiles)
            }
        }

        return {
            launcher.launch(
                arrayOf(
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
            )
        }
    }

   private fun getFileName(uri: Uri, context: Context): String {
        var name = "unknown"

        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && index != -1) {
                name = it.getString(index)
            }
        }

        return name
    }
}
