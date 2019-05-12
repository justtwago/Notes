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

    protected fun enterConfirmationMode() {
        confirmationMode.value = true
    }

    protected fun exitConfirmationMode() {
        confirmationMode.value = false
    }

    @CallSuper
    override fun onCleared() {
        coroutineContext.cancel()
    }
}