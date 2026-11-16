package com.nmnvan.patches.moonsift.shared

import app.morphe.patcher.patch.bytecodePatch
import com.nmnvan.patches.moonsift.shared.Constants.EXTENSION_BUNDLE

/**
 * Adds the extension code. No setup call is needed: the hooks pass in everything they use.
 */
internal val sharedExtensionPatch = bytecodePatch {
    extendWith(EXTENSION_BUNDLE)
}
