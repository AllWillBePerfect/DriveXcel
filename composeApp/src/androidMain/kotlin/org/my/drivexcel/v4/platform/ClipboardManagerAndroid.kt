package org.my.drivexcel.v4.platform

import android.content.ClipData
import android.content.Context
import org.my.drivexcel.ui.platform.ClipboardManager

class ClipboardManagerAndroid(
    private val context: Context
) : ClipboardManager {
    override fun copy(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE)
                as android.content.ClipboardManager

        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }
}