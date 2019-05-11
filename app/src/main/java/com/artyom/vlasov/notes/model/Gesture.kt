package com.artyom.vlasov.notes.model

sealed class Gesture {
    sealed class Swipe : Gesture() {
        object Left : Swipe()
        object Right : Swipe()
    }

    sealed class Tap : Gesture() {
        object LongPress : Tap()

        sealed class Single : Tap() {
            object SingleFinger : Single()
            object DoubleFinger : Single()
        }

        sealed class Double : Tap() {
            object SingleFinger : Double()
            object DoubleFinger : Double()
        }
    }
}