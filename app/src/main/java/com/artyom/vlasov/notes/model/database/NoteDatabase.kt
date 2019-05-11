package com.artyom.vlasov.notes.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.artyom.vlasov.notes.model.database.entities.Note

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase: RoomDatabase() {

    abstract fun noteDao(): NoteDao
}