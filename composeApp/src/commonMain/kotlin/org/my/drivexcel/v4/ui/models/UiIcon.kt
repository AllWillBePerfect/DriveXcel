package org.my.drivexcel.v4.ui.models

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.DrawableResource

sealed interface UiIcon {
    data class Vector(val imageVector: ImageVector) : UiIcon
    data class Drawable(val res: DrawableResource) : UiIcon
}