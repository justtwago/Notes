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

    private fun setCurrentNote(index: Int) {
        allNotes.getOrNull(index)?.run {
            currentNoteTitle.postValue(title)
            currentNoteText.postValue(note)
        }
    }

    fun onNextNoteCalled() {
        currentNoteIndex++
        if (currentNoteIndex == allNotes.size) currentNoteIndex = 0
        setCurrentNote(currentNoteIndex)
    }

    fun onPreviousNoteCalled() {
        currentNoteIndex--
        if (currentNoteIndex == -1) currentNoteIndex = allNotes.size - 1
        setCurrentNote(currentNoteIndex)
    }
}
