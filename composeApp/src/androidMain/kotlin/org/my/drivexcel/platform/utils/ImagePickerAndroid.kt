package org.my.drivexcel.platform.utils

import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext



class ImagePickerAndroid : ImagePicker {

    private var launcher: ManagedActivityResultLauncher<String, android.net.Uri?>? = null
    private var context: Context? = null
    private var onImageSelected: ((ByteArray) -> Unit)? = null

    @Composable
    override fun PickImage(onImageSelected: (ByteArray) -> Unit) {
        val ctx = LocalContext.current
        context = ctx
        this.onImageSelected = onImageSelected

        val launcherRemembered = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                val bytes = ctx.contentResolver.openInputStream(it)?.use { input -> input.readBytes() }
                bytes?.let { onImageSelected(it) }
            }
        }

        SideEffect {
            launcher = launcherRemembered
        }

        Button(onClick = { launcherRemembered.launch("image/*") }) {
            Text("Выбрать изображение")
        }
    }

    override fun launchPicker() {
        launcher?.launch("image/*")
    }
}
