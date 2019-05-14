package com.artyom.vlasov.notes.ui.notes

import androidx.lifecycle.MutableLiveData
import com.artyom.vlasov.notes.model.AssistantInstructionProvider
import com.artyom.vlasov.notes.model.Gesture
import com.artyom.vlasov.notes.model.SingleLiveEvent
import com.artyom.vlasov.notes.model.database.entities.Note
import com.artyom.vlasov.notes.model.repository.DatabaseRepository
import com.artyom.vlasov.notes.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(
    private val databaseRepository: DatabaseRepository,
    private val assistantInstructionProvider: AssistantInstructionProvider
) : BaseViewModel() {

    private val allNotes = mutableListOf<Note>()
    private var currentNoteIndex = 0

    val openNoteDetailsEvent = SingleLiveEvent<Int>()
    val stopAssistantVoice = SingleLiveEvent<Unit>()
    val listenPickedNote = SingleLiveEvent<Note>()
    val assistantInstructions = MutableLiveData<List<String>>()
    val currentNoteTitle = MutableLiveData<String>()
    val currentNoteText = MutableLiveData<String>()
    val onGestureDetectedListener: (Gesture) -> Unit = { gesture ->
        launch(Dispatchers.Main) { stopAssistantVoice.call() }
        when (gesture) {
            Gesture.Swipe.Right -> onSwipeRightDetected()
            Gesture.Swipe.Left -> onSwipeLeftDetected()
            Gesture.Tap.LongPress -> onLongPressDetected()
            Gesture.Tap.Double.DoubleFinger -> onDoubleTapDoubleFingerDetected()
            Gesture.Tap.Double.SingleFinger -> onDoubleTapSingleFingerDetected()
            Gesture.Tap.Single.DoubleFinger -> onSingleTapDoubleFingerDetected()
            Gesture.Tap.Single.SingleFinger -> onSingleTapSingleFingerDetected()
        }
    }

    init {
        launch {
            databaseRepository.insertNote(Note(0, "One one one", "one one one"), Note(1, "Two two two", "Two two two"))
            allNotes.addAll(databaseRepository.getAllNotes())
            setCurrentNote(currentNoteIndex)
            callAssistantInstructions()
        }
    }

    override fun callAssistantInstructions() {
        val assistantState = when {
            confirmationMode -> AssistantState.CONFIRMATION_MODE
            allNotes.isEmpty() -> AssistantState.EMPTY_NOTES
            allNotes.size == 1 -> AssistantState.SINGLE_NOTE
            else -> AssistantState.DEFAULT
        }

        val listenInstructions = assistantInstructionProvider.listenInstructions
        val pickPreviousNote = when (assistantState) {
            AssistantState.DEFAULT -> assistantInstructionProvider.pickPreviousNote
            else -> ""
        }
        val pickNextNote = when (assistantState) {
            AssistantState.DEFAULT -> assistantInstructionProvider.pickNextNote
            else -> ""
        }
        val createNote = when (assistantState) {
            AssistantState.CONFIRMATION_MODE -> ""
            else -> assistantInstructionProvider.createNote
        }
        val listenNote = when (assistantState) {
            AssistantState.DEFAULT -> assistantInstructionProvider.listenNote
            AssistantState.SINGLE_NOTE -> assistantInstructionProvider.listenNote
            else -> ""
        }
        val editNote = when (assistantState) {
            AssistantState.DEFAULT -> assistantInstructionProvider.editNote
            AssistantState.SINGLE_NOTE -> assistantInstructionProvider.editNote
            else -> ""
        }
        val deleteNote = when (assistantState) {
            AssistantState.DEFAULT -> assistantInstructionProvider.deleteNote
            AssistantState.SINGLE_NOTE -> assistantInstructionProvider.deleteNote
            else -> ""
        }
        val confirmDeleting = when (assistantState) {
            AssistantState.CONFIRMATION_MODE -> assistantInstructionProvider.confirmDeleting
            else -> ""
        }
        val cancelDeleting = when (assistantState) {
            AssistantState.CONFIRMATION_MODE -> assistantInstructionProvider.cancelDeleting
            else -> ""
        }

        assistantInstructions.postValue(
            listOf(
                pickPreviousNote,
                pickNextNote,
                createNote,
                listenNote,
                editNote,
                deleteNote,
                confirmDeleting,
                cancelDeleting,
                listenInstructions
            )
        )
    }

    private fun onSwipeRightDetected() {
        if (confirmationMode) {
            removeCurrentNote()
        } else {
            showNextNote()
            listenPickedNote.postValue(allNotes.getOrNull(currentNoteIndex))
        }
    }

    private fun showNextNote() {
        currentNoteIndex++
        if (currentNoteIndex == allNotes.size) currentNoteIndex = 0
        setCurrentNote(currentNoteIndex)
    }

    private fun onSwipeLeftDetected() {
        if (confirmationMode) {
            exitConfirmationMode()
        } else {
            showPreviousNote()
            listenPickedNote.postValue(allNotes.getOrNull(currentNoteIndex))
        }
    }

    private fun onLongPressDetected() {
        callAssistantInstructions()
    }

    private fun showPreviousNote() {
        currentNoteIndex--
        if (currentNoteIndex == -1) currentNoteIndex = allNotes.size - 1
        setCurrentNote(currentNoteIndex)
    }

    private fun onDoubleTapDoubleFingerDetected() {
        if (allNotes.isNotEmpty()) {
            enterConfirmationMode()
        }
    }

    private fun removeCurrentNote() {
        launch {
            allNotes.getOrNull(currentNoteIndex)?.run {
                databaseRepository.deleteNote(this)
                allNotes.remove(this)
                updateNotesAfterDeleting()
            }
            exitConfirmationMode()
        }
    }

    private fun updateNotesAfterDeleting() {
        when {
            allNotes.isEmpty() -> resetCurrentNote()
            allNotes.size == currentNoteIndex -> setCurrentNote(--currentNoteIndex)
            else -> setCurrentNote(currentNoteIndex)
        }
    }

    private fun setCurrentNote(index: Int) {
        allNotes.getOrNull(index)?.run {
            currentNoteTitle.postValue(title)
            currentNoteText.postValue(text)
        }
    }

    private fun resetCurrentNote() {
        currentNoteTitle.postValue("")
        currentNoteText.postValue("")
    }

    private fun onDoubleTapSingleFingerDetected() {
        openNoteDetailsEvent.value = Note.UNDEFINED_ID
    }

    private fun onSingleTapDoubleFingerDetected() {
        if (allNotes.isNotEmpty()) {
            openNoteDetailsEvent.postValue(currentNoteIndex)
        }
    }

    private fun onSingleTapSingleFingerDetected() {
        listenPickedNote.postValue(allNotes.getOrNull(currentNoteIndex))
    }

    enum class AssistantState {
        SINGLE_NOTE, EMPTY_NOTES, CONFIRMATION_MODE, DEFAULT
    }
}
