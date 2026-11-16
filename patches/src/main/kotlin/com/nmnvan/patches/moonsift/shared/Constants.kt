package com.nmnvan.patches.moonsift.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Constants {
    val COMPATIBILITY_MOONSIFT = Compatibility(
        name = "Moonsift",
        packageName = "com.moonsift.wish.list.maker.christmas.birthday.baby.shopping.gift.list",
        apkFileType = ApkFileType.APK,
        appIconColor = 0x000000,
        targets = listOf(
            AppTarget(version = "1.31"),
        )
    )

    /** Extension bundle with the Java code the patches call into. */
    const val EXTENSION_BUNDLE = "extensions/moonsift.mpe"

    const val EXTENSION_PACKAGE = "Lcom/nmnvan/extension/moonsift"

    /** WebView client of the logged-in screen, which shows moonsift.com. */
    const val WEB_VIEW_CLIENT_CLASS = "Lcom/moonsift/app/ui/loggedin/LoggedInComposeables\$CustomWebViewClient;"
}
