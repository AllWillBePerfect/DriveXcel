package org.my.drivexcel.platform

import java.nio.file.Path

interface RootDirPathProvider {
    fun provide(): Path
}