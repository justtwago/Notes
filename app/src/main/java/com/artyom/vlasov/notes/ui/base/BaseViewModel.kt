package com.artyom.vlasov.notes.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    private lateinit var coroutineScope: CoroutineScope

    fun initCoroutineScope(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
    }

    fun launch(action: suspend () -> Unit) {
        coroutineScope.launch {
            action.invoke()
        }
    }

    @ExperimentalCoroutinesApi
    override fun onCleared() {
        coroutineScope.cancel()
        super.onCleared()
    }
}