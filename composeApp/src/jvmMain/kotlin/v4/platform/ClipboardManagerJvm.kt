package v4.platform

import org.my.drivexcel.ui.platform.ClipboardManager
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class ClipboardManagerJvm : ClipboardManager {
    override fun copy(text: String) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val selection = StringSelection(text)
        clipboard.setContents(selection, selection)
    }
}