package com.artyom.vlasov.notes.model.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Voice(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "path") val path: String
)