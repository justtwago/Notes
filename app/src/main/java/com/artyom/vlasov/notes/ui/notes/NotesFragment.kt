package com.artyom.vlasov.notes.ui.notes

import com.artyom.vlasov.notes.R
import com.artyom.vlasov.notes.databinding.FragmentNotesBinding
import com.artyom.vlasov.notes.model.Gesture
import com.artyom.vlasov.notes.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotesFragment : BaseFragment<FragmentNotesBinding>() {
    override val layoutId = R.layout.fragment_notes
    override val viewModel by viewModel<NotesViewModel>()

    override fun setupBindingVariables(binding: FragmentNotesBinding) {
        binding.viewModel = viewModel
    }

    override fun onGestureDetected(gesture: Gesture) {
        when (gesture) {
            Gesture.Swipe.Right -> viewModel.onSwipeRight()
            Gesture.Swipe.Left -> viewModel.onSwipeLeft()
            Gesture.Tap.Double.DoubleFinger -> viewModel.onDoubleTapDoubleFinger()
        }
    }
}