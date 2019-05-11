package com.artyom.vlasov.notes.di

import androidx.room.Room
import com.artyom.vlasov.notes.model.database.NoteDatabase
import com.artyom.vlasov.notes.model.repository.DatabaseRepository
import com.artyom.vlasov.notes.model.repository.DatabaseRepositoryImpl
import com.artyom.vlasov.notes.ui.details.NoteDetailsViewModel
import com.artyom.vlasov.notes.ui.notes.NotesViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import kotlin.math.sin

val appModule = module {

    single {
        Room.databaseBuilder(androidApplication(), NoteDatabase::class.java, "note-database")
            .build()
            .noteDao()
    }

    single {
        DatabaseRepositoryImpl(noteDao = get()) as DatabaseRepository
    }

    viewModel { NotesViewModel() }
    viewModel { NoteDetailsViewModel() }
}