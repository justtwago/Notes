package com.artyom.vlasov.notes.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + SupervisorJob() }

    protected var isConfirmationMode = false

    abstract fun callAssistantInstructions()

    protected fun enterConfirmationMode() {
        isConfirmationMode = true
        callAssistantInstructions()
    }

    protected fun exitConfirmationMode() {
        isConfirmationMode = false
        callAssistantInstructions()
    }

    @CallSuper
    override fun onCleared() {
        coroutineContext.cancel()
    }
}