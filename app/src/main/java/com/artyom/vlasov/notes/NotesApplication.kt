package com.artyom.vlasov.notes

import android.app.Application
import com.artyom.vlasov.notes.di.appModule
import org.koin.android.ext.android.startKoin

class NotesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
    }
}