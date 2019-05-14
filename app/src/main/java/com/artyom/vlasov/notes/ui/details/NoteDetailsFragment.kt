package com.artyom.vlasov.notes.ui.details

import android.app.Activity.RESULT_OK
import android.speech.tts.TextToSpeech
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.artyom.vlasov.notes.R
import com.artyom.vlasov.notes.databinding.FragmentNoteDetailsBinding
import com.artyom.vlasov.notes.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.speech.RecognizerIntent
import android.content.Intent
import java.util.*

private const val TITLE_VOICE_RECOGNIZER_REQUEST_CODE = 8
private const val TEXT_VOICE_RECOGNIZER_REQUEST_CODE = 9

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
        viewModel.recordNoteTitle.observe(viewLifecycleOwner, Observer {
            openVoiceRecognizer(TITLE_VOICE_RECOGNIZER_REQUEST_CODE)
        })
        viewModel.recordNoteText.observe(viewLifecycleOwner, Observer {
            openVoiceRecognizer(TEXT_VOICE_RECOGNIZER_REQUEST_CODE)
        })
    }

    private fun speakTextWithQueue(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val recognizedText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (resultCode == RESULT_OK && !recognizedText.isNullOrBlank()) {
            when (requestCode) {
                TITLE_VOICE_RECOGNIZER_REQUEST_CODE -> viewModel.onTitleRecognized(recognizedText)
                TEXT_VOICE_RECOGNIZER_REQUEST_CODE -> viewModel.onTextRecognized(recognizedText)
            }
        }
    }

    private fun openVoiceRecognizer(requestCode: Int) {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
            startActivityForResult(this, requestCode)
        }
    }
}