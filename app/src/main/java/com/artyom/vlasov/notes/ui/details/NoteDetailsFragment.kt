package com.artyom.vlasov.notes.ui.details

import com.artyom.vlasov.notes.R
import com.artyom.vlasov.notes.databinding.FragmentNoteDetailsBinding
import com.artyom.vlasov.notes.model.Gesture
import com.artyom.vlasov.notes.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class NoteDetailsFragment : BaseFragment<FragmentNoteDetailsBinding>() {
    override val layoutId = R.layout.fragment_notes
    override val viewModel by viewModel<NoteDetailsViewModel>()

    override fun setupBindingVariables(binding: FragmentNoteDetailsBinding) {
        binding.viewModel = viewModel
    }

    override fun onGestureDetected(gesture: Gesture) = Unit
}