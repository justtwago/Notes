package com.artyom.vlasov.notes.ui.notes

import android.speech.tts.TextToSpeech
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.artyom.vlasov.notes.R
import com.artyom.vlasov.notes.databinding.FragmentNotesBinding
import com.artyom.vlasov.notes.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class NotesFragment : BaseFragment<FragmentNotesBinding>() {
    override val layoutId = R.layout.fragment_notes
    override val viewModel by viewModel<NotesViewModel>()

    override fun setupBindingVariables(binding: FragmentNotesBinding) {
        binding.viewModel = viewModel
    }

    override fun onTextToSpeechReady() {
        registerObservers()
        textToSpeech.language = Locale.ENGLISH
    }

    private fun registerObservers() {
        viewModel.openNoteDetailsEvent.observe(viewLifecycleOwner, Observer {
            val direction = NotesFragmentDirections.actionNotesToDetails(it)
            findNavController().navigate(direction)
        })
        viewModel.assistantInstructions.observe(viewLifecycleOwner, Observer {instructions ->
            instructions.forEach {
                textToSpeech.speak(it, TextToSpeech.QUEUE_ADD, null, "")
            }
        })
        viewModel.stopAssistantVoice.observe(viewLifecycleOwner, Observer {
            textToSpeech.stop()
        })
    }
}