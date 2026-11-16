package com.nmnvan.patches.moonsift.shared

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.extensions.InstructionExtensions.removeInstruction
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod

/**
 * Inserts code before the instruction at [index], so that branches which jump to that
 * instruction run the inserted code too. (Plain insertion leaves branch labels on the
 * original instruction, skipping the new code.)
 */
internal fun MutableMethod.addInstructionsAtControlFlowLabel(index: Int, smaliInstructions: String) {
    // Duplicate the original after itself, put the new code between, then remove the original.
    // Its label moves to the next instruction, which is the first new one.
    addInstruction(index + 1, getInstruction(index))
    addInstructionsWithLabels(index + 1, smaliInstructions)
    removeInstruction(index)
}
