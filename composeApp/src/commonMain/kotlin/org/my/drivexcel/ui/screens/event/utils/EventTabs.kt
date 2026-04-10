package org.my.drivexcel.ui.screens.event.utils

enum class EventTabs {
    Users,
    Import
}

fun EventTabs.toName() = when (this) {
    EventTabs.Users -> "Пользователи"
    EventTabs.Import -> "Импорт"
}