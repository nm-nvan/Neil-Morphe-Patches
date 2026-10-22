package com.nmnvan.patches.thescore.ads

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod
import com.nmnvan.patches.thescore.shared.Constants.COMPATIBILITY_THESCORE
import com.nmnvan.patches.thescore.shared.Constants.EXTENSION_PACKAGE
import com.nmnvan.patches.thescore.shared.sharedExtensionPatch

private const val EXTENSION_CLASS = "$EXTENSION_PACKAGE/patches/HideAdsPatch;"

/** Makes a suspend ad loader report "no ad" (Boolean.FALSE) without requesting anything. */
private fun MutableMethod.returnNoAd() = addInstructions(
    0,
    """
        sget-object v0, Ljava/lang/Boolean;->FALSE:Ljava/lang/Boolean;
        return-object v0
    """
)

@Suppress("unused")
val hideSplashAdPatch = bytecodePatch(
    name = "Hide splash ad",
    description = "Removes the sponsored image or video shown while the app starts.",
    default = true
) {
    compatibleWith(COMPATIBILITY_THESCORE)

    execute {
        // A null SplashAd is the app's own "nothing to show" path; startup continues normally.
        GetSplashAdFingerprint.method.addInstructions(
            0,
            """
                const/4 v0, 0x0
                return-object v0
            """
        )
    }
}

@Suppress("unused")
val hideHeaderBannerAdsPatch = bytecodePatch(
    name = "Hide header banner ads",
    description = "Removes the banner ad under the header on game screens (also on the Scores and topic headers).",
    default = true
) {
    compatibleWith(COMPATIBILITY_THESCORE)

    execute {
        // The masthead container is wrap_content and stays empty (zero height) when no ad loads.
        MastheadAdLoadFingerprint.method.returnNoAd()
        // Deferred preloading was added in 26.15.0; older versions only have the loader above.
        MastheadAdDeferredLoadFingerprint.methodOrNull?.returnNoAd()
    }
}

@Suppress("unused")
val hideInlineListAdsPatch = bytecodePatch(
    name = "Hide inline list ads",
    description = "Removes the large ads and banners mixed into lists such as Scores, News and Scorecast.",
    default = true
) {
    compatibleWith(COMPATIBILITY_THESCORE)

    dependsOn(sharedExtensionPatch)

    execute {
        // Drop ad items before the adapter diffs the list, so no empty slots are left behind.
        ListAdapterSetDataFingerprint.method.addInstructions(
            0,
            """
                invoke-static { p1 }, $EXTENSION_CLASS->filterListAds(Ljava/util/List;)Ljava/util/List;
                move-result-object p1
            """
        )

        // Second layer: never request big box ads even if an item slips through.
        BigBoxAdLoadFingerprint.method.returnNoAd()
    }
}

@Suppress("unused")
val hideSponsorAdsPatch = bytecodePatch(
    name = "Hide sponsor ads",
    description = "Removes \"Presented by\" sponsor ads, for example on Standings and in articles.",
    default = true
) {
    compatibleWith(COMPATIBILITY_THESCORE)

    execute {
        PresentedByAdLoadFingerprint.method.returnNoAd()
    }
}
