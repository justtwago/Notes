package com.artyom.vlasov.notes.model.repository

import com.artyom.vlasov.notes.model.database.NoteDao
import com.artyom.vlasov.notes.model.database.entities.Note

interface DatabaseRepository {
    suspend fun getAllNotes(): List<Note>

    suspend fun insertNote(vararg note: Note)

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(note: Note)
}

class DatabaseRepositoryImpl(val noteDao: NoteDao) : DatabaseRepository {

    override suspend fun getAllNotes(): List<Note> {
        return noteDao.getAll()
    }

    override suspend fun insertNote(vararg note: Note) {
        noteDao.insert(*note)
    }

    override suspend fun updateNote(note: Note) {
        noteDao.update(note)
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }
}
