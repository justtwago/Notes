package com.artyom.vlasov.notes.ui.details

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.artyom.vlasov.notes.R
import com.artyom.vlasov.notes.databinding.FragmentNoteDetailsBinding
import com.artyom.vlasov.notes.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class NoteDetailsFragment : BaseFragment<FragmentNoteDetailsBinding>() {
    override val layoutId = R.layout.fragment_note_details
    override val viewModel by viewModel<NoteDetailsViewModel>()

    override fun setupBindingVariables(binding: FragmentNoteDetailsBinding) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val noteId = NoteDetailsFragmentArgs.fromBundle(arguments!!).noteId
        viewModel.initialize(noteId)
        registerObservers()
    }

    private fun registerObservers() {
        viewModel.openNotesEvent.observe(viewLifecycleOwner, Observer {
            val direction = NoteDetailsFragmentDirections.actionDetailsToNotes()
            findNavController().navigate(direction)
        })
    }
}