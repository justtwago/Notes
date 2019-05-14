package com.artyom.vlasov.notes.model

import android.content.Context
import com.artyom.vlasov.notes.R

class AssistantInstructionProvider(context: Context) {
    val listenInstructions = context.getString(R.string.instruction_long_press, context.getString(R.string.listen_instruction))
    val pickPreviousNote = context.getString(R.string.instruction_swipe_left, context.getString(R.string.pick_previous_note))
    val pickNextNote = context.getString(R.string.instruction_swipe_right, context.getString(R.string.pick_next_note))
    val listenNote = context.getString(R.string.instruction_single_tap_single_finger, context.getString(R.string.listen_note))
    val editNote = context.getString(R.string.instruction_single_tap_double_finger, context.getString(R.string.edit_note))
    val createNote = context.getString(R.string.instruction_double_tap_single_finger, context.getString(R.string.create_note))
    val deleteNote = context.getString(R.string.instruction_double_tap_double_finger, context.getString(R.string.delete_note))
    val confirmDeleting = context.getString(R.string.instruction_swipe_right, context.getString(R.string.confirm_deleting))
    val cancelDeleting = context.getString(R.string.instruction_swipe_left, context.getString(R.string.cancel_deleting))
}