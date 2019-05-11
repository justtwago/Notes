package com.artyom.vlasov.notes.model.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "note")
data class Note(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "note") val note: String,
    @ColumnInfo(name = "date") val date: Date,
    @ColumnInfo(name = "title-voice") val titleVoice: Voice,
    @ColumnInfo(name = "note-voice") val noteVoice: Voice
)