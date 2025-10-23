package org.my.drivexcel.models

/**
 * Варианты входа в приложение
 *
 * @param LOCAL - данные на компьютере
 * @param GOOGLE - данные на Google диске
 * @param YANDEX - данные на Yandex диске
 */
enum class LoginType {
    LOCAL, GOOGLE, YANDEX
}