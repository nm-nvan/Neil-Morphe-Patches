package com.nmnvan.patches.moonsift.links

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.nmnvan.patches.moonsift.shared.Constants.COMPATIBILITY_MOONSIFT
import com.nmnvan.patches.moonsift.shared.Constants.EXTENSION_PACKAGE
import com.nmnvan.patches.moonsift.shared.addInstructionsAtControlFlowLabel
import com.nmnvan.patches.moonsift.shared.sharedExtensionPatch

private const val EXTENSION_METHOD =
    "$EXTENSION_PACKAGE/patches/SanitizeLinksPatch;->sanitize(Landroid/net/Uri;)Landroid/net/Uri;"

private const val EXTENSION_OVERRIDE_URL_LOADING =
    "$EXTENSION_PACKAGE/patches/SanitizeLinksPatch;->overrideUrlLoading(Landroid/webkit/WebView;Landroid/webkit/WebResourceRequest;)Z"

private const val APP_PACKAGE = "Lcom/moonsift/"

/**
 * WebViewClient of the add-to-collection browser. It returns false, so links load in place
 * instead of going through an Intent.
 */
internal object AddCollectionShouldOverrideUrlLoadingFingerprint : Fingerprint(
    definingClass = "Lcom/moonsift/app/ui/addCollection/AddCollectionWebViewComposeables\$CustomWebViewClient;",
    name = "shouldOverrideUrlLoading",
    returnType = "Z",
    parameters = listOf("Landroid/webkit/WebView;", "Landroid/webkit/WebResourceRequest;"),
)

/**
 * Calls that open a link outside the app. In each one the Uri is the third register,
 * after the receiver and one other argument.
 *
 * - `new Intent(action, uri)`: MainActivity opens product links (popup labels) this way, as does AndroidMoonLauncher.
 * - `CustomTabsIntent.launchUrl(context, uri)`: links in HTML text.
 */
private val LINK_OPENING_CALLS = listOf(
    Triple("Landroid/content/Intent;", "<init>", listOf("Ljava/lang/String;", "Landroid/net/Uri;")),
    Triple(
        "Landroidx/browser/customtabs/CustomTabsIntent;",
        "launchUrl",
        listOf("Landroid/content/Context;", "Landroid/net/Uri;")
    ),
)

private fun Instruction.isLinkOpeningCall(): Boolean {
    val reference = (this as? ReferenceInstruction)?.reference as? MethodReference ?: return false
    return LINK_OPENING_CALLS.any { (definingClass, name, parameters) ->
        reference.definingClass == definingClass &&
            reference.name == name &&
            reference.parameterTypes.map { it.toString() } == parameters
    }
}

/** The register holding the Uri: the third register of the call. */
private fun Instruction.uriRegister() = when (this) {
    is FiveRegisterInstruction -> registerE
    is RegisterRangeInstruction -> startRegister + 2
    else -> throw PatchException("Unexpected instruction format: $opcode")
}

private fun Method.hasSameSignature(other: Method) =
    name == other.name && parameterTypes.map { it.toString() } == other.parameterTypes.map { it.toString() }

@Suppress("unused")
val sanitizeLinksPatch = bytecodePatch(
    name = "Sanitize outbound links",
    description = "Opens product and other outbound links without affiliate redirects (Skimlinks, VigLink, digidip) and removes tracking parameters such as utm_ tags and Amazon affiliate tags.",
    default = true
) {
    compatibleWith(COMPATIBILITY_MOONSIFT)

    dependsOn(sharedExtensionPatch)

    execute {
        // Find every app method that opens a link, then clean its Uri just before each such call.
        val targets = buildList {
            classDefForEach { classDef ->
                if (!classDef.type.startsWith(APP_PACKAGE)) return@classDefForEach
                val methods = classDef.methods.filter { method ->
                    method.implementation?.instructions?.any { it.isLinkOpeningCall() } == true
                }
                if (methods.isNotEmpty()) add(classDef to methods)
            }
        }

        var hooked = 0
        targets.forEach { (classDef, methods) ->
            val mutableClass = mutableClassDefBy(classDef)
            methods.forEach { method ->
                val mutableMethod = mutableClass.methods.first { it.hasSameSignature(method) }
                val indices = mutableMethod.implementation!!.instructions
                    .withIndex()
                    .filter { it.value.isLinkOpeningCall() }
                    .map { it.index }

                // Work backwards so earlier indices stay valid.
                indices.asReversed().forEach { index ->
                    val register = mutableMethod.getInstruction(index).uriRegister()
                    mutableMethod.addInstructionsAtControlFlowLabel(
                        index,
                        """
                            invoke-static/range { v$register .. v$register }, $EXTENSION_METHOD
                            move-result-object v$register
                        """
                    )
                    hooked++
                }
            }
        }

        if (hooked == 0) throw PatchException("No link-opening calls found in $APP_PACKAGE classes")

        // In-app browser: swap affiliate redirects for the clean link before they load.
        // v0 is free at the start of the method (the original code writes it before reading it).
        AddCollectionShouldOverrideUrlLoadingFingerprint.method.addInstructionsWithLabels(
            0,
            """
                invoke-static/range { p1 .. p2 }, $EXTENSION_OVERRIDE_URL_LOADING
                move-result v0
                if-eqz v0, :run_original
                const/4 v0, 0x1
                return v0
                :run_original
                nop
            """
        )
    }
}
