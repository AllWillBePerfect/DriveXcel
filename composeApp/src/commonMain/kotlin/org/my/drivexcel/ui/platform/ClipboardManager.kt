package org.my.drivexcel.ui.platform

interface ClipboardManager {
    fun copy(text: String)
}

interface ClipboardManagerProvider {
    fun provide(): ClipboardManager
}