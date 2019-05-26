package olehakaminskyi.livestreamfails.player

import android.content.Context

import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player.EventListener
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.Player.STATE_BUFFERING
import com.google.android.exoplayer2.Player.STATE_IDLE
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.Player.REPEAT_MODE_OFF
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.source.MediaSource
import org.junit.Test
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import olehakaminskyi.livestreamfails.player.VideoPlayerController.State.Idle
import olehakaminskyi.livestreamfails.player.VideoPlayerController.State.Buffering
import olehakaminskyi.livestreamfails.player.VideoPlayerController.State.Ready
import olehakaminskyi.livestreamfails.tests.CoroutinesTestRule
import org.junit.Assert
import org.junit.Rule
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ExoVideoPlayerControllerTest {

    @get:Rule
    val testCarolineRule = CoroutinesTestRule()

    @Mock
    lateinit var player: ExoPlayer
    @Mock
    lateinit var sourceConverter: UrlSourceConverter

    private lateinit var playerController: ExoVideoPlayerController

    @Before
    fun setup() {
        playerController = ExoVideoPlayerController(
            mock(Context::class.java), player,
            _sourceConverter = sourceConverter
        )
        `when`(sourceConverter.convert(any(), any())).thenReturn(mock(MediaSource::class.java))
    }

    @Test
    fun setRepeatMode() = runBlockingTest {
        playerController.repeatMode = VideoPlayerController.RepeatMode.NONE
        verify(player, times(1)).repeatMode = REPEAT_MODE_OFF
        playerController.repeatMode = VideoPlayerController.RepeatMode.ONE
        verify(player, times(1)).repeatMode = REPEAT_MODE_ONE
    }

    @Test
    fun play() = runBlockingTest {
        val source = UrlSource("some url source")
        playerController.play(source)
        verify(player, atLeastOnce()).prepare(any())
        verify(player, atLeastOnce()).playWhenReady = true
    }

    @Test
    fun stop() = runBlockingTest {
        playerController.stop()
        verify(player, times(1)).stop(true)
    }

    @Test
    fun resume() = runBlockingTest {
        playerController.resume()
        verify(player, times(1)).playWhenReady = true
    }

    @Test
    fun pause() = runBlockingTest {
        playerController.pause()
        verify(player, times(1)).playWhenReady = false
    }

    @Test
    fun addListener() = runBlockingTest {
        val source = UrlSource("some url source")
        val valueHolder = object {
            lateinit var eventListener: EventListener
        }
        val listener = mock(VideoPlayerController.Listener::class.java)
        `when`(player.addListener(any())).thenAnswer {
            valueHolder.eventListener = it.arguments[0] as EventListener
            null
        }

        // states population
        Assert.assertEquals(playerController.state, Idle)
        playerController.play(source)
        playerController.addListener(listener)

        // error
        valueHolder.eventListener.onPlayerError(
            ExoPlaybackException.createForSource(IOException())
        )
        verify(listener, times(1)).onStateChanged(any(), any())

        // Idle
        valueHolder.eventListener.onPlayerStateChanged(true, STATE_IDLE)
        verify(listener, times(1)).onStateChanged(source, Idle)

        // Buffering
        valueHolder.eventListener.onPlayerStateChanged(true, STATE_BUFFERING)
        verify(listener, times(1)).onStateChanged(source, Buffering)

        // Ready
        valueHolder.eventListener.onPlayerStateChanged(true, STATE_READY)
        verify(listener, times(1)).onStateChanged(source, Ready)

        // Ended
        valueHolder.eventListener.onPlayerStateChanged(true, STATE_ENDED)
        verify(listener, times(1)).onStateChanged(source, Ready)
    }

    @Test
    fun removeListener() = runBlockingTest {
        val source = UrlSource("some url source")
        val valueHolder = object {
            lateinit var eventListener: EventListener
        }
        val listener = mock(VideoPlayerController.Listener::class.java)
        `when`(player.addListener(any())).thenAnswer {
            valueHolder.eventListener = it.arguments[0] as EventListener
            null
        }

        playerController.play(source)
        playerController.addListener(listener)
        playerController.removeListener(listener)

        // error
        valueHolder.eventListener.onPlayerError(
            ExoPlaybackException.createForSource(IOException())
        )

        // loading true
        valueHolder.eventListener.onLoadingChanged(true)

        // loading false
        valueHolder.eventListener.onLoadingChanged(false)

        arrayOf(STATE_IDLE, STATE_BUFFERING, STATE_READY, STATE_ENDED).forEach {
            valueHolder.eventListener.onPlayerStateChanged(true, it)
        }

        verify(listener, never()).onStateChanged(eq(source), any())
    }
}