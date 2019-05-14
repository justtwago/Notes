package com.artyom.vlasov.notes.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + SupervisorJob() }

    private val confirmationMode = MutableLiveData<Boolean>()
    protected val isConfirmationMode: Boolean
        get() = confirmationMode.value == true

    abstract fun callAssistantInstructions()

    protected fun enterConfirmationMode() {
        confirmationMode.postValue(true)
        callAssistantInstructions()
    }

    protected fun exitConfirmationMode() {
        confirmationMode.postValue(false)
        callAssistantInstructions()
    }

    @CallSuper
    override fun onCleared() {
        coroutineContext.cancel()
    }
}