package olehakaminskyi.livestreamfails.presentation.action

import androidx.annotation.UiThread

class OneTimeAction {
    private var isExecuted: Boolean = false
    @UiThread
    fun processValue(function: () -> Unit) {
        if (!isExecuted) {
            function()
        }
        isExecuted = true
    }
}