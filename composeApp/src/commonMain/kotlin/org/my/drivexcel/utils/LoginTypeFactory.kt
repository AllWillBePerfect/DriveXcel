package org.my.drivexcel.utils

import org.my.drivexcel.models.LoginType
import org.my.drivexcel.platform.utils.AppLogger

class LoginTypeFactory(

) {

    // TODO Make Realization
    private val loginType: LoginType = LoginType.LOCAL

    /**
     * Возвращает имя директории в зависимости от текущего способа входа в приложение
     */
    fun rootDirectoryName(): String = when (loginType) {
        LoginType.LOCAL -> AppLogger.LOCAL_ROOT_DIRECTORY
        LoginType.GOOGLE -> AppLogger.GOOGLE_ROOT_DIRECTORY
        LoginType.YANDEX -> AppLogger.YANDEX_ROOT_DIRECTORY
    }

}