package com.artyom.vlasov.notes.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + SupervisorJob() }

    protected var confirmationMode = false

    abstract fun callAssistantInstructions()

    protected fun enterConfirmationMode() {
        confirmationMode = true
        callAssistantInstructions()
    }

    protected fun exitConfirmationMode() {
        confirmationMode = false
        callAssistantInstructions()
    }

    @CallSuper
    override fun onCleared() {
        coroutineContext.cancel()
    }
}