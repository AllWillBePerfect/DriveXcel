package org.my.drivexcel.ui.models

sealed interface UiText {
    data class Text(val text: String) : UiText
    data class AnnotatedString(val annotatedString: androidx.compose.ui.text.AnnotatedString) : UiText
}