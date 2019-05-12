package com.artyom.vlasov.notes.managers

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
    view: View,
    private val onGestureListener: (Gesture) -> Unit
) : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private var beginTouchTime = 0L
    private var doubleTapCounter = 0
    private var tapDelayJob: Job? = null
    private var skipNextInvocation = false
    private var startX = 0f
    private var startY = 0f
    private var isAfterActionPointerUp = false

    private val gestureCompatDetector = GestureDetectorCompat(view.context, this)

    init {
        gestureCompatDetector.setOnDoubleTapListener(this)
        view.setOnTouchListener { _, event ->
            handleSwipeAndMultiFingerTapListener(event, onGestureListener)
            gestureCompatDetector.onTouchEvent(event)
        }
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

    private fun handleSwipeAndMultiFingerTapListener(event: MotionEvent, onGestureListener: (Gesture) -> Unit) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_UP -> {
                if (isAfterActionPointerUp) {
                    isAfterActionPointerUp = false
                    return
                }
                val swipedHorizontally = Math.abs(event.x - startX) > DEFAULT_SWIPED_THRESHOLD

                if (swipedHorizontally) {
                    val swipedRight = event.x > startX
                    val swipedLeft = event.x < startX

                    if (swipedRight) {
                        onGestureListener.invoke(Gesture.Swipe.Right)
                    }
                    if (swipedLeft) {
                        onGestureListener.invoke(Gesture.Swipe.Left)
                    }
                }
                startX = 0f
                startY = 0f
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                beginTouchTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                isAfterActionPointerUp = true
                val releasedTouchDuration = System.currentTimeMillis() - beginTouchTime
                when {
                    event.pointerCount > 2 -> skipNextInvocation = true
                    event.pointerCount == 2 && skipNextInvocation -> skipNextInvocation = false
                    releasedTouchDuration < 500 && event.pointerCount == 2 && !skipNextInvocation -> {
                        doubleTapCounter++
                        if (doubleTapCounter < 2) {
                            tapDelayJob = GlobalScope.launch {
                                delay(300)
                                onGestureListener.invoke(Gesture.Tap.Single.DoubleFinger)
                                doubleTapCounter = 0
                            }
                        } else {
                            tapDelayJob?.cancel()
                            onGestureListener.invoke(Gesture.Tap.Double.DoubleFinger)
                            doubleTapCounter = 0
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val DEFAULT_SWIPED_THRESHOLD = 100

        fun runDetecting(view: View, onGestureListener: (Gesture) -> Unit) {
            MultiFingerGestureDetector(view, onGestureListener)
        }
    }
}