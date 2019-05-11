package com.artyom.vlasov.notes.di

import com.artyom.vlasov.notes.ui.details.NoteDetailsViewModel
import com.artyom.vlasov.notes.ui.notes.NotesViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {
    viewModel { NotesViewModel() }
    viewModel { NoteDetailsViewModel() }
}