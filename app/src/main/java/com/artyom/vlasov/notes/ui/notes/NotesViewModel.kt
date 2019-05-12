package com.artyom.vlasov.notes.ui.notes

import androidx.lifecycle.MutableLiveData
import com.artyom.vlasov.notes.model.database.entities.Note
import com.artyom.vlasov.notes.model.repository.DatabaseRepository
import com.artyom.vlasov.notes.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class NotesViewModel(val databaseRepository: DatabaseRepository) : BaseViewModel() {
    val currentNoteTitle = MutableLiveData<String>()
    val currentNoteText = MutableLiveData<String>()

    private val allNotes = mutableListOf<Note>()
    private var currentNoteIndex = 0

    init {
        launch {
            allNotes.addAll(databaseRepository.getAllNotes())
            setCurrentNote(currentNoteIndex)
        }
    }

    fun onSwipeRight() {
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

    fun onSwipeLeft() {
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

    fun onDoubleTapDoubleFinger() {
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
}
