package org.my.drivexcel.v4.ui.screens.event.utils

enum class EventTabs {
    Users,
    Import
}

fun EventTabs.toName() = when (this) {
    EventTabs.Users -> "Пользователи"
    EventTabs.Import -> "Импорт"
}