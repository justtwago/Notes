package com.artyom.vlasov.notes.model.database

import androidx.room.*
import androidx.room.OnConflictStrategy.*
import com.artyom.vlasov.notes.model.database.entities.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM note")
    fun getAll(): List<Note>

    @Delete
    fun delete(note: Note)

    @Insert(onConflict = REPLACE)
    fun insert(vararg note: Note)

    @Update
    fun update(note: Note)

}