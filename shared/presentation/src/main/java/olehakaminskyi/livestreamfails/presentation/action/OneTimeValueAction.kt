package olehakaminskyi.livestreamfails.presentation.action

import androidx.annotation.UiThread

class OneTimeValueAction<T> (private val _value: T) {
    private var isExecuted: Boolean = false

    @UiThread
    fun processValue(function: (T) -> Unit) {
        if (!isExecuted) {
            function(_value)
        }
        isExecuted = true
    }
}