package com.artyom.vlasov.notes.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.artyom.vlasov.notes.managers.MultiFingerGestureDetector
import com.artyom.vlasov.notes.model.Gesture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment<B : ViewDataBinding> : Fragment(), CoroutineScope {
    private lateinit var binding: B
    protected abstract val layoutId: Int
    protected abstract val viewModel: BaseViewModel

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        setupBindingVariables(binding)
        return binding.root.apply { MultiFingerGestureDetector.runDetecting(this, ::onGestureDetected) }
    }

    abstract fun setupBindingVariables(binding: B)

    abstract fun onGestureDetected(gesture: Gesture)

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}