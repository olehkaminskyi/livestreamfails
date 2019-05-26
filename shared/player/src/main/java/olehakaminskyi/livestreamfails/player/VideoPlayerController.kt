package olehakaminskyi.livestreamfails.player

data class UrlSource(val url: String = "")

interface VideoPlayerController {

    var repeatMode: RepeatMode
    val state: State
    fun play(urlSource: UrlSource)
    fun pause()
    fun resume()
    fun stop()
    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)

    enum class RepeatMode {
        ONE, NONE
    }

    interface Listener {
        fun onStateChanged(urlSource: UrlSource, state: State)
    }

    sealed class State {
        object Idle : State()
        object Buffering : State()
        object Ready : State()
        object Ended : State()
        class Error(val message: String) : State()
    }
}
