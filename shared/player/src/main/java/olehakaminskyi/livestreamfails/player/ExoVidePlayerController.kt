package olehakaminskyi.livestreamfails.player

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.Player.REPEAT_MODE_OFF
import com.google.android.exoplayer2.Player.STATE_IDLE
import com.google.android.exoplayer2.Player.STATE_BUFFERING
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.Player.EventListener
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import olehakaminskyi.livestreamfails.player.VideoPlayerController.State
import olehakaminskyi.livestreamfails.player.VideoPlayerController.RepeatMode

class ExoVideoPlayerController(
    private val _context: Context,
    private val _player: ExoPlayer,
    private val _sourceConverter: UrlSourceConverter = DefaultUrlSourceConverter
) : VideoPlayerController, CoroutineScope by CoroutineScope(Dispatchers.Main) {
    override var repeatMode: RepeatMode = RepeatMode.NONE
        set(value) {
            _player.repeatMode = when (value) {
                RepeatMode.ONE -> REPEAT_MODE_ONE
                RepeatMode.NONE -> REPEAT_MODE_OFF
            }
            field = value
        }

    private val _listeners: MutableList<VideoPlayerController.Listener> = mutableListOf()
    private var playerListener: EventListener? = null

    override fun play(urlSource: UrlSource) {
        launch {
            _player.apply {
                if (playerListener != null) {
                    removeListener(playerListener)
                }
                addListener(createPlayerListener(urlSource).also { playerListener = it })
                prepare(_sourceConverter.convert(urlSource, _context))
                playWhenReady = true
            }
        }
    }

    override fun stop() {
        launch {
            _player.stop(true)
        }
    }

    override fun resume() {
        launch {
            _player.playWhenReady = true
        }
    }

    override fun pause() {
        launch {
            _player.playWhenReady = false
        }
    }

    private var _state: State = State.Idle
    override val state
        get() = _state

    override fun addListener(listener: VideoPlayerController.Listener) {
        launch {
            _listeners.add(listener)
        }
    }

    override fun removeListener(listener: VideoPlayerController.Listener) {
        launch {
            _listeners.remove(listener)
        }
    }

    private fun createPlayerListener(videoSource: UrlSource) = object : EventListener {

        override fun onPlayerError(error: ExoPlaybackException?) {
            notifyStateChange(videoSource, State.Error(error?.message ?: "Unknown error"))
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                STATE_IDLE -> notifyStateChange(videoSource, State.Idle)
                STATE_BUFFERING -> notifyStateChange(videoSource, State.Buffering)
                STATE_READY -> notifyStateChange(videoSource, State.Ready)
                STATE_ENDED -> notifyStateChange(videoSource, State.Ended)
            }
        }
    }

    private fun notifyStateChange(videoSource: UrlSource, state: State) {
        launch {
            _state = state
            _listeners.forEach { it.onStateChanged(videoSource, state) }
        }
    }
}

interface UrlSourceConverter {
    fun convert(urlSource: UrlSource, context: Context): MediaSource
}

object DefaultUrlSourceConverter : UrlSourceConverter {
    override fun convert(urlSource: UrlSource, context: Context): MediaSource {
        val userAgent = Util.getUserAgent(context, "${context.packageName}#media")
        return ExtractorMediaSource
            .Factory(DefaultHttpDataSourceFactory(userAgent))
            .setExtractorsFactory(DefaultExtractorsFactory())
            .createMediaSource(Uri.parse(urlSource.url))
    }
}