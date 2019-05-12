package com.artyom.vlasov.notes.ui.notes

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.artyom.vlasov.notes.R
import com.artyom.vlasov.notes.databinding.FragmentNotesBinding
import com.artyom.vlasov.notes.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotesFragment : BaseFragment<FragmentNotesBinding>() {
    override val layoutId = R.layout.fragment_notes
    override val viewModel by viewModel<NotesViewModel>()

    override fun setupBindingVariables(binding: FragmentNotesBinding) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObservers()
    }

    private fun registerObservers() {
        viewModel.openNoteDetailsEvent.observe(viewLifecycleOwner, Observer {
            val direction = NotesFragmentDirections.actionNotesToDetails(it)
            findNavController().navigate(direction)
        })
    }
}