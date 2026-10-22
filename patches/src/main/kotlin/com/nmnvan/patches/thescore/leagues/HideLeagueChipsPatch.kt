package com.nmnvan.patches.thescore.leagues

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.patch.bytecodePatch
import com.nmnvan.patches.thescore.settings.settingsPatch
import com.nmnvan.patches.thescore.shared.Constants.COMPATIBILITY_THESCORE
import com.nmnvan.patches.thescore.shared.Constants.EXTENSION_PACKAGE
import com.nmnvan.patches.thescore.shared.isFrom
import com.nmnvan.patches.thescore.shared.setExtensionIsPatchIncluded

private const val EXTENSION_CLASS = "$EXTENSION_PACKAGE/patches/LeaguesScreenPatch;"

/**
 * LeaguesMenuTransformer: builds the recent-league chip row (an empty list or a single chips item).
 */
internal object RecentLeagueChipsFingerprint : Fingerprint(
    returnType = "Ljava/lang/Object;",
    parameters = listOf("Lcom/thescore/repositories/data/meta/LeaguesMeta;", "L"),
    strings = listOf("RECENT_LEAGUES"),
    custom = { _, classDef -> classDef.isFrom("LeaguesMenuTransformer.kt") }
)

@Suppress("unused")
val hideLeagueChipsPatch = bytecodePatch(
    name = "Hide league chips",
    description = "Adds an option in Morphe Settings to hide the row of recently viewed leagues on the Leagues tab.",
    default = true
) {
    compatibleWith(COMPATIBILITY_THESCORE)

    dependsOn(settingsPatch)

    execute {
        setExtensionIsPatchIncluded(EXTENSION_CLASS)

        // An empty list is the app's own "no recent leagues" result.
        RecentLeagueChipsFingerprint.method.addInstructionsWithLabels(
            0,
            """
                invoke-static { }, $EXTENSION_CLASS->hideLeagueChips()Z
                move-result v0
                if-eqz v0, :show
                invoke-static { }, Ljava/util/Collections;->emptyList()Ljava/util/List;
                move-result-object v0
                return-object v0
                :show
                nop
            """
        )
    }
}
