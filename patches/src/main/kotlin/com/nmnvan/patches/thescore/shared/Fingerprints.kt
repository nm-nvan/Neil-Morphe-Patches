package com.nmnvan.patches.thescore.shared

import com.android.tools.smali.dexlib2.iface.ClassDef

/**
 * theScore is obfuscated with R8, but classes keep their Kotlin SourceFile attribute
 * (e.g. "MastheadAdManagerDelegate.kt"). Matching on it is far more stable across
 * app versions than obfuscated class names.
 */
internal fun ClassDef.isFrom(sourceFileName: String) = sourceFile == sourceFileName
