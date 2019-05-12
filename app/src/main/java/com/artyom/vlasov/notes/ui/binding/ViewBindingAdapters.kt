package com.artyom.vlasov.notes.ui.binding

import android.view.View
import androidx.databinding.BindingAdapter
import com.artyom.vlasov.notes.managers.MultiFingerGestureDetector
import com.artyom.vlasov.notes.model.Gesture

@BindingAdapter("onGestureDetectedListener")
fun View.setGestureDetection(onGestureDetected: (Gesture) -> Unit) {
    MultiFingerGestureDetector.runDetecting(this, onGestureDetected)
}