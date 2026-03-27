package org.my.drivexcel.v4.ui.models

sealed interface UiText {
    data class Text(val text: String) : UiText
    data class AnnotatedString(val annotatedString: androidx.compose.ui.text.AnnotatedString) : UiText
}