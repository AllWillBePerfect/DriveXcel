package org.my.drivexcel.ui.screens.settings

import org.my.drivexcel.base.domain.model.NightModeModel

fun NightModeModel.toName() = when (this) {
    NightModeModel.NIGHT -> "Темная"
    NightModeModel.DAY -> "Светлая"
    NightModeModel.FOLLOW_SYSTEM -> "Системная"
}