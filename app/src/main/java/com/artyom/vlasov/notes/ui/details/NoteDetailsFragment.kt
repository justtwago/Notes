package com.artyom.vlasov.notes.ui.details

import android.speech.tts.TextToSpeech
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

    override fun onTextToSpeechReady() {
        val noteId = NoteDetailsFragmentArgs.fromBundle(arguments!!).noteId
        viewModel.initialize(noteId)
        registerObservers()
    }

    private fun registerObservers() {
        viewModel.openNotesEvent.observe(viewLifecycleOwner, Observer {
            val direction = NoteDetailsFragmentDirections.actionDetailsToNotes()
            findNavController().navigate(direction)
        })
        viewModel.assistantInstructions.observe(viewLifecycleOwner, Observer { instructions ->
            instructions.forEach(::speakTextWithQueue)
        })
        viewModel.stopAssistantVoice.observe(viewLifecycleOwner, Observer {
            textToSpeech.stop()
        })
        viewModel.listenCurrentNote.observe(viewLifecycleOwner, Observer { note ->
            note?.apply {
                if (!title.isBlank()) {
                    speakTextWithQueue(getString(R.string.note_title))
                    speakTextWithQueue(title)
                }
                if (!text.isBlank()) {
                    speakTextWithQueue(getString(R.string.note_text))
                    speakTextWithQueue(text)
                }
            }
        })
    }

    private fun speakTextWithQueue(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, "")
    }
}