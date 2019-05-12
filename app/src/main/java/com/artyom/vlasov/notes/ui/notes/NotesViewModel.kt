package com.artyom.vlasov.notes.ui.notes

import androidx.lifecycle.MutableLiveData
import com.artyom.vlasov.notes.model.Gesture
import com.artyom.vlasov.notes.model.SingleLiveEvent
import com.artyom.vlasov.notes.model.database.entities.Note
import com.artyom.vlasov.notes.model.repository.DatabaseRepository
import com.artyom.vlasov.notes.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class NotesViewModel(private val databaseRepository: DatabaseRepository) : BaseViewModel() {
    private val allNotes = mutableListOf<Note>()
    private var currentNoteIndex = 0

    val openNoteDetailsEvent = SingleLiveEvent<Int>()
    val currentNoteTitle = MutableLiveData<String>()
    val currentNoteText = MutableLiveData<String>()
    val onGestureDetectedListener: (Gesture) -> Unit = { gesture ->
        when (gesture) {
            Gesture.Swipe.Right -> onSwipeRight()
            Gesture.Swipe.Left -> onSwipeLeft()
            Gesture.Tap.Double.DoubleFinger -> onDoubleTapDoubleFinger()
            Gesture.Tap.Double.SingleFinger -> onDoubleTapSingleFinger()
            Gesture.Tap.Single.DoubleFinger -> onSingleTapDoubleFinger()
        }
    }

    init {
        launch {
            allNotes.addAll(databaseRepository.getAllNotes())
            setCurrentNote(currentNoteIndex)
        }
    }

    private fun onSwipeRight() {
        if (isConfirmationMode) {
            removeCurrentNote()
            exitConfirmationMode()
        } else {
            showNextNote()
        }
    }

    private fun showNextNote() {
        currentNoteIndex++
        if (currentNoteIndex == allNotes.size) currentNoteIndex = 0
        setCurrentNote(currentNoteIndex)
    }

    private fun onSwipeLeft() {
        if (isConfirmationMode) {
            exitConfirmationMode()
        } else {
            showPreviousNote()
        }
    }

    private fun showPreviousNote() {
        currentNoteIndex--
        if (currentNoteIndex == -1) currentNoteIndex = allNotes.size - 1
        setCurrentNote(currentNoteIndex)
    }

    private fun onDoubleTapDoubleFinger() {
        enterConfirmationMode()
    }

    private fun removeCurrentNote() {
        launch {
            allNotes.getOrNull(currentNoteIndex)?.run {
                databaseRepository.deleteNote(this)
                allNotes.remove(this)
                updateNotesAfterDeleting()
            }
        }
    }

    private fun updateNotesAfterDeleting() {
        when {
            allNotes.isEmpty() -> resetCurrentNote()
            allNotes.size == currentNoteIndex && allNotes.size != 1 -> setCurrentNote(currentNoteIndex - 1)
            else -> setCurrentNote(currentNoteIndex)
        }
    }

    private fun setCurrentNote(index: Int) {
        allNotes.getOrNull(index)?.run {
            currentNoteTitle.postValue(title)
            currentNoteText.postValue(note)
        }
    }

    private fun resetCurrentNote() {
        currentNoteTitle.postValue("")
        currentNoteText.postValue("")
    }

    private fun onDoubleTapSingleFinger() {
        openNoteDetailsEvent.value = Note.UNDEFINED_ID
    }

    private fun onSingleTapDoubleFinger() {
        if (allNotes.isNotEmpty()) {
            openNoteDetailsEvent.postValue(currentNoteIndex)
        }
    }
}
