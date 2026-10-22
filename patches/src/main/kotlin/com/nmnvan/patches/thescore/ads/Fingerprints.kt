package com.nmnvan.patches.thescore.ads

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.nmnvan.patches.thescore.shared.isFrom

/**
 * AdStorageGateway.getSplashAd(): returns a SplashAd or null.
 * Picks the video or image splash depending on a remote flag.
 */
internal object GetSplashAdFingerprint : Fingerprint(
    returnType = "Ljava/lang/Object;",
    parameters = listOf("L"),
    strings = listOf(
        "location_region_code",
        "location_country",
        "com.thescore.animated_launch_screens",
    ),
    custom = { _, classDef -> classDef.isFrom("AdStorageGateway.kt") }
)

/**
 * AdManagerDelegate.loadAd(Context, AdParams, Continuation<Boolean>): Object.
 * Every ad type delegate overrides this; returning Boolean.FALSE is the normal "no fill" result.
 */
private fun adDelegateLoadFingerprint(sourceFile: String) = Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Ljava/lang/Object;",
    parameters = listOf("Landroid/content/Context;", "L", "L"),
    custom = { method, classDef ->
        // The override takes the AdParams base type. Some delegates have a same-shaped
        // helper that takes a nested AdParams subtype (e.g. "Llr$a;"), which must not match.
        classDef.isFrom(sourceFile) && !method.parameterTypes[1].contains('$')
    }
)

/**
 * DeferredAdLoader.load(MainActivity, AdParams.Page, Continuation<Boolean>): Object.
 * Second entry point used to preload the masthead. Not present in 26.12.0.
 */
private fun adDelegateDeferredLoadFingerprint(sourceFile: String) = Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Ljava/lang/Object;",
    parameters = listOf("Lcom/thescore/base/ui/MainActivity;", "L", "L"),
    custom = { _, classDef -> classDef.isFrom(sourceFile) }
)

internal val MastheadAdLoadFingerprint = adDelegateLoadFingerprint("MastheadAdManagerDelegate.kt")
internal val MastheadAdDeferredLoadFingerprint = adDelegateDeferredLoadFingerprint("MastheadAdManagerDelegate.kt")
internal val BigBoxAdLoadFingerprint = adDelegateLoadFingerprint("BigBoxAdManagerDelegate.kt")
internal val PresentedByAdLoadFingerprint = adDelegateLoadFingerprint("PresentedByAdManagerDelegate.kt")

/**
 * ListAdapter.setData(List<? extends AdapterItem>): the shared RecyclerView adapter that
 * every list screen (Scores, News, league lists, Scorecast) uses.
 */
internal object ListAdapterSetDataFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "V",
    parameters = listOf("Ljava/util/List;"),
    custom = { _, classDef ->
        classDef.isFrom("ListAdapter.kt") &&
            classDef.superclass == "Landroidx/recyclerview/widget/RecyclerView\$Adapter;"
    }
)
