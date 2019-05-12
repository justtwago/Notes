package com.artyom.vlasov.notes.managers

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import com.artyom.vlasov.notes.model.Gesture
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MultiFingerGestureDetector private constructor(
    context: Context,
    private val onGestureListener: (Gesture) -> Unit
) : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private val gestureCompat = GestureDetectorCompat(context, this)

    init {
        gestureCompat.setOnDoubleTapListener(this)
    }

    override fun onShowPress(e: MotionEvent?) {
        return
    }

    override fun onSingleTapUp(event: MotionEvent): Boolean {
        return true
    }

    override fun onDown(e: MotionEvent?): Boolean = false

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
        onGestureListener.invoke(Gesture.Tap.LongPress)
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        when (event.pointerCount) {
            1 -> onGestureListener.invoke(Gesture.Tap.Double.SingleFinger)
            2 -> onGestureListener.invoke(Gesture.Tap.Double.DoubleFinger)
        }
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
        when (event.pointerCount) {
            1 -> onGestureListener.invoke(Gesture.Tap.Single.SingleFinger)
            2 -> onGestureListener.invoke(Gesture.Tap.Single.DoubleFinger)
        }
        return true
    }

    companion object {
        private var beginTouchTime = 0L
        private var tapCounter = 0
        private var tapDelayJob: Job? = null
        private var skipNextInvocation = false

        fun runDetecting(view: View, onGestureListener: (Gesture) -> Unit) {
            val detector = GestureDetectorCompat(
                view.context,
                MultiFingerGestureDetector(view.context, onGestureListener)
            )

            view.setOnTouchListener { _, event ->
                handleMultiFingerTouchListener(event, onGestureListener)
                detector.onTouchEvent(event)
            }
        }

        private fun handleMultiFingerTouchListener(event: MotionEvent, onGestureListener: (Gesture) -> Unit) {
            when (event.actionMasked) {
                MotionEvent.ACTION_POINTER_DOWN -> {
                    beginTouchTime = System.currentTimeMillis()
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    val releasedTouchDuration = System.currentTimeMillis() - beginTouchTime
                    when {
                        event.pointerCount > 2 -> skipNextInvocation = true
                        event.pointerCount == 2 && skipNextInvocation -> skipNextInvocation = false
                        releasedTouchDuration < 500 && event.pointerCount == 2 && !skipNextInvocation -> {
                            tapCounter++
                            if (tapCounter < 2) {
                                tapDelayJob = GlobalScope.launch {
                                    delay(300)
                                    onGestureListener.invoke(Gesture.Tap.Single.DoubleFinger)
                                    tapCounter = 0
                                }
                            } else {
                                tapDelayJob?.cancel()
                                onGestureListener.invoke(Gesture.Tap.Double.DoubleFinger)
                                tapCounter = 0
                            }
                        }
                    }
                }
            }
        }
    }
}