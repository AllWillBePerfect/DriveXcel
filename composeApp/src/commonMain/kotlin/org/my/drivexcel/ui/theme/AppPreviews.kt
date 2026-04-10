package org.my.drivexcel.ui.theme

import org.jetbrains.compose.ui.tooling.preview.Preview


/**
 * Спецификация устройства для превью с нужной высотой и шириной
 */
const val PREVIEW_DEVICE = "spec:width=360dp,height=732dp,dpi=416"

@Preview(name = "phone", widthDp = 360, heightDp = 640)
@Preview(name = "small_phone", widthDp = 300, heightDp = 640)
@Preview(name = "tight_phone", widthDp = 240, heightDp = 640)
@Preview(name = "landscape", widthDp = 640, heightDp = 360)
@Preview(name = "foldable", widthDp = 673, heightDp = 841)
@Preview(name = "tablet", widthDp = 840, heightDp = 800)
@Preview(name = "desktop", widthDp = 1280, heightDp = 800)
annotation class AppPreviews

@Preview(name = "tight_phone", widthDp = 240, heightDp = 640)
annotation class TightPhonePreview

@Preview(name = "small_phone", widthDp = 360, heightDp = 640)
annotation class SmallPhonePreview

@Preview(name = "phone", widthDp = 360, heightDp = 640)
annotation class PhonePreview

@Preview(name = "landscape", widthDp = 640, heightDp = 360)
annotation class LandscapePreview

@Preview(name = "foldable", widthDp = 673, heightDp = 841)
annotation class FoldablePreview

@Preview(name = "tablet", widthDp = 840, heightDp = 800)
annotation class TabletPreview

@Preview(name = "desktop", widthDp = 1280, heightDp = 800)
annotation class DesktopPreview
