package com.artyom.vlasov.notes.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + SupervisorJob() }

    @CallSuper
    override fun onCleared() {
        coroutineContext.cancel()
    }
}