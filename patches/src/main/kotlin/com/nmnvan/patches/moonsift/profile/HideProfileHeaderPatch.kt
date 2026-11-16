package com.nmnvan.patches.moonsift.profile

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.nmnvan.patches.moonsift.shared.Constants.COMPATIBILITY_MOONSIFT
import com.nmnvan.patches.moonsift.shared.Constants.EXTENSION_PACKAGE
import com.nmnvan.patches.moonsift.shared.Constants.WEB_VIEW_CLIENT_CLASS
import com.nmnvan.patches.moonsift.shared.sharedExtensionPatch

private const val EXTENSION_CLASS = "$EXTENSION_PACKAGE/patches/ProfileHeaderPatch;"

internal object WebViewOnPageFinishedFingerprint : Fingerprint(
    definingClass = WEB_VIEW_CLIENT_CLASS,
    name = "onPageFinished",
    returnType = "V",
    parameters = listOf("Landroid/webkit/WebView;", "Ljava/lang/String;"),
)

@Suppress("unused")
val hideProfileHeaderPatch = bytecodePatch(
    name = "Hide profile header",
    description = "Hides the avatar, name and username at the top of the home screen, so it opens straight to your collections.",
    default = true
) {
    compatibleWith(COMPATIBILITY_MOONSIFT)

    dependsOn(sharedExtensionPatch)

    execute {
        // The home screen is moonsift.com in a WebView: inject a style rule after every page load.
        WebViewOnPageFinishedFingerprint.method.addInstruction(
            0,
            "invoke-static/range { p1 .. p1 }, $EXTENSION_CLASS->onPageFinished(Landroid/webkit/WebView;)V"
        )
    }
}
