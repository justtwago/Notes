package com.artyom.vlasov.notes.ui.details

import androidx.lifecycle.MutableLiveData
import com.artyom.vlasov.notes.model.Gesture
import com.artyom.vlasov.notes.model.SingleLiveEvent
import com.artyom.vlasov.notes.model.database.entities.Note
import com.artyom.vlasov.notes.model.repository.DatabaseRepository
import com.artyom.vlasov.notes.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class NoteDetailsViewModel(private val databaseRepository: DatabaseRepository) : BaseViewModel() {
    val openNotesEvent = SingleLiveEvent<Unit>()
    val currentNoteTitle = MutableLiveData<String>()
    val currentNoteText = MutableLiveData<String>()
    val onGestureDetectedListener: (Gesture) -> Unit = { gesture ->
        when (gesture) {
            Gesture.Swipe.Right -> onSwipeRight()
            Gesture.Swipe.Left -> onSwipeLeft()
            Gesture.Tap.Double.SingleFinger -> onDoubleTapSingleFinger()
        }
    }

    fun initialize(noteId: Int) {
        if (noteId != Note.UNDEFINED_ID) {
            launch {
                val note = databaseRepository.getNote(noteId)
                currentNoteTitle.postValue(note.title)
                currentNoteText.postValue(note.note)
            }
        }
    }

    private fun onDoubleTapSingleFinger() {
        enterConfirmationMode()
    }

    private fun onSwipeLeft() {
        exitConfirmationMode()
        openNotesEvent.call()
    }

    private fun onSwipeRight() {
        exitConfirmationMode()
        openNotesEvent.call()
    }
}