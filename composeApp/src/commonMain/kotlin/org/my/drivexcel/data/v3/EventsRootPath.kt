package org.my.drivexcel.data.v3

import java.nio.file.Path

interface RootDirPathProvider {
    fun provide(): Path
}