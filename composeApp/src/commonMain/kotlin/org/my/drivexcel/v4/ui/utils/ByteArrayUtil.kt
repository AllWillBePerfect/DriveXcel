package org.my.drivexcel.v4.ui.utils

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import javax.imageio.ImageIO

 fun createGradientImageByteArray(): ByteArray {
    val width = 1920
    val height = 1080

    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    for (y in 0 until height) {
        val ratio = y.toFloat() / height

        val r = (255 * (1 - ratio)).toInt()
        val g = (128 * ratio).toInt()
        val b = 255

        val color = Color(r, g, b).rgb

        for (x in 0 until width) {
            image.setRGB(x, y, color)
        }
    }

    val stream = ByteArrayOutputStream()
    ImageIO.write(image, "png", stream)

    return stream.toByteArray()
}

fun ByteArray.sha256(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(this)
    return hash.joinToString("") { "%02x".format(it) }
}