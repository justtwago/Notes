package com.artyom.vlasov.notes.ui.details

import androidx.lifecycle.MutableLiveData
import com.artyom.vlasov.notes.model.AssistantInstructionProvider
import com.artyom.vlasov.notes.model.Gesture
import com.artyom.vlasov.notes.model.SingleLiveEvent
import com.artyom.vlasov.notes.model.database.entities.Note
import com.artyom.vlasov.notes.model.repository.DatabaseRepository
import com.artyom.vlasov.notes.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class NoteDetailsViewModel(
    private val databaseRepository: DatabaseRepository,
    private val assistantInstructionProvider: AssistantInstructionProvider
) : BaseViewModel() {

    private var noteId = Note.UNDEFINED_ID
    val openNotesEvent = SingleLiveEvent<Unit>()
    val stopAssistantVoice = SingleLiveEvent<Unit>()
    val listenCurrentNote = SingleLiveEvent<Note>()
    val recordNoteTitle = SingleLiveEvent<Unit>()
    val recordNoteText = SingleLiveEvent<Unit>()
    val currentNoteTitle = MutableLiveData<String>()
    val currentNoteText = MutableLiveData<String>()
    val assistantInstructions = MutableLiveData<List<String>>()
    val onGestureDetectedListener: (Gesture) -> Unit = { gesture ->
        launch(Dispatchers.Main) { stopAssistantVoice.call() }
        when (gesture) {
            Gesture.Swipe.Right -> onSwipeRightDetected()
            Gesture.Swipe.Left -> onSwipeLeftDetected()
            Gesture.Tap.Single.DoubleFinger -> onDoubleTapSingleFingerDetected()
            Gesture.Tap.LongPress -> onLongPressDetected()
            Gesture.Tap.Single.SingleFinger -> onSingleTapSingleFingerDetected()
        }
    }

    fun initialize(noteId: Long) {
        callAssistantInstructions()
        this.noteId = noteId
        if (noteId != Note.UNDEFINED_ID) {
            launch {
                val note = databaseRepository.getNote(noteId)
                currentNoteTitle.postValue(note.title)
                currentNoteText.postValue(note.text)
            }
        }
    }

    override fun callAssistantInstructions() {
        with(assistantInstructionProvider) {
            assistantInstructions.postValue(
                if (isConfirmationMode) {
                    listOf(
                        saveNote,
                        discardNoteChanges
                    )
                } else {
                    listOf(
                        noteDetailsLocation,
                        recordNoteTitle,
                        recordNoteText,
                        listenNote,
                        returnToNoteList,
                        listenInstructions
                    )
                }
            )
        }
    }

    fun onTitleRecognized(title: String) {
        currentNoteTitle.value = title
        onSingleTapSingleFingerDetected()
    }

    fun onTextRecognized(text: String) {
        currentNoteText.value = text
        onSingleTapSingleFingerDetected()
    }

    private fun onDoubleTapSingleFingerDetected() {
        enterConfirmationMode()
    }

    private fun onSwipeLeftDetected() {
        if (isConfirmationMode) {
            openNotesEvent.call()
        } else {
            recordNoteTitle.call()
        }
    }

    private fun onSwipeRightDetected() {
        if (isConfirmationMode) {
            launch {
                saveNote()
                launch(Dispatchers.Main) { openNotesEvent.call() }
            }
        } else {
            recordNoteText.call()
        }
    }

    private fun onLongPressDetected() {
        callAssistantInstructions()
    }

    private fun onSingleTapSingleFingerDetected() {
        listenCurrentNote.postValue(
            Note(
                id = noteId,
                title = currentNoteTitle.value.orEmpty(),
                text = currentNoteText.value.orEmpty()
            )
        )
    }

    private suspend fun saveNote() {
        val title = currentNoteTitle.value.orEmpty()
        val text = currentNoteText.value.orEmpty()
        if (!(title.isBlank() && text.isBlank())) {
            if (noteId == Note.UNDEFINED_ID) {
                databaseRepository.insertNote(
                    Note(
                        id = Calendar.getInstance().timeInMillis,
                        title = title,
                        text = text
                    )
                )
            } else {
                databaseRepository.updateNote(
                    Note(
                        id = noteId,
                        title = title,
                        text = text
                    )
                )
            }
        }
    }

}