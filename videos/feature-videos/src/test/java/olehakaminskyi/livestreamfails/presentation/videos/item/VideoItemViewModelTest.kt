package olehakaminskyi.livestreamfails.presentation.videos.item

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.whenever
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.eq
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.ErrorType
import olehakaminskyi.livestreamfails.domain.ResultError
import olehakaminskyi.livestreamfails.domain.videos.VideosRepository
import olehakaminskyi.livestreamfails.domain.videos.entities.Video
import olehakaminskyi.livestreamfails.player.UrlSource
import olehakaminskyi.livestreamfails.player.VideoPlayerController
import olehakaminskyi.livestreamfails.tests.CoroutinesTestRule
import olehakaminskyi.livestreamfails.tests.assertEmpty
import olehakaminskyi.livestreamfails.tests.assertNotEmpty
import olehakaminskyi.livestreamfails.tests.assertValue
import org.junit.Before
import org.junit.Test

import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class VideoItemViewModelTest {

    private lateinit var viewModel: VideoItemViewModel
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    @Mock
    lateinit var videosRepository: VideosRepository
    @Mock
    lateinit var videoPlayerController: VideoPlayerController
    val postItemId = 100L

    @Before
    fun setup() = runBlockingTest {
        viewModel = VideoItemViewModel(videoPlayerController, videosRepository, postItemId)
    }

    @Test
    fun getReadyToPlay_loadError() = runBlockingTest {
        val errorResult = DataResult<Video>(error = ResultError(ErrorType.Unknown))
        whenever(videosRepository.getVideoForPost(postItemId)).doReturn(errorResult)
        viewModel.readyToPlay.assertEmpty()
        viewModel.loadingState.assertValue(LoadingState.Error(ErrorCause.DATA))
        viewModel.onStateChanged(UrlSource("videoUrl"), VideoPlayerController.State.Idle)
        viewModel.readyToPlay.assertValue(false)
    }

    @Test
    fun getReadyToPlay_loadSuccess() = runBlockingTest {
        val successResult = DataResult(Video("videoUrl"))
        whenever(videosRepository.getVideoForPost(postItemId)).doReturn(successResult)
        whenever(videoPlayerController.state).doReturn(VideoPlayerController.State.Ready)
        viewModel.readyToPlay.assertValue(false)
        viewModel.loadingState.assertValue(LoadingState.Loading)
        viewModel.onStateChanged(UrlSource("other url"), VideoPlayerController.State.Idle)
        viewModel.readyToPlay.assertValue(true)
        viewModel.onStateChanged(UrlSource(successResult.data!!.url),
            VideoPlayerController.State.Ready)
        viewModel.loadingState.assertValue(LoadingState.Ready)
    }

    @Test
    fun play() = runBlockingTest {
        val successResult = DataResult(Video("videoUrl"))
        whenever(videosRepository.getVideoForPost(postItemId)).doReturn(successResult)
        viewModel.readyToPlay.assertNotEmpty()
        viewModel.play()
        verify(videoPlayerController, times(1))
            .play(eq(UrlSource(successResult.data!!.url)))
    }

    @Test
    fun stop() = runBlockingTest {
        val video = Video("videoUrl")
        val successResult = DataResult(video)
        whenever(videosRepository.getVideoForPost(postItemId)).doReturn(successResult)
        viewModel.readyToPlay.assertNotEmpty()

        viewModel.stop()
        verify(videoPlayerController, never()).stop()

        viewModel.onStateChanged(UrlSource(video.url), VideoPlayerController.State.Ready)
        viewModel.stop()
        verify(videoPlayerController, times(1)).stop()
    }

    @Test
    fun pause() = runBlockingTest {
        val video = Video("videoUrl")
        val successResult = DataResult(video)
        whenever(videosRepository.getVideoForPost(postItemId)).doReturn(successResult)
        viewModel.readyToPlay.assertNotEmpty()

        viewModel.pause()
        verify(videoPlayerController, never()).pause()

        viewModel.onStateChanged(UrlSource(video.url), VideoPlayerController.State.Ready)
        viewModel.pause()
        verify(videoPlayerController, times(1)).pause()
    }

    @Test
    fun resume() = runBlockingTest {
        val video = Video("videoUrl")
        val successResult = DataResult(video)
        whenever(videosRepository.getVideoForPost(postItemId)).doReturn(successResult)
        viewModel.readyToPlay.assertNotEmpty()

        viewModel.resume()
        verify(videoPlayerController, never()).resume()

        viewModel.onStateChanged(UrlSource(video.url), VideoPlayerController.State.Ready)
        viewModel.resume()
        verify(videoPlayerController, times(1)).resume()
    }

    @Test
    fun onCleared() = runBlockingTest {
        val successResult = DataResult(Video("videoUrl"))
        whenever(videosRepository.getVideoForPost(postItemId)).doReturn(successResult)
        viewModel.readyToPlay.assertNotEmpty()
        viewModel.onCleared()
        verify(videoPlayerController, times(1)).removeListener(viewModel)
    }

    @Test
    fun onStateChanged() = runBlockingTest {
        val successResult = DataResult(Video("videoUrl"))
        val currentUrlSource = UrlSource(successResult.data!!.url)
        val otherUrlSource = UrlSource("other url")
        whenever(videosRepository.getVideoForPost(postItemId)).doReturn(successResult)
        whenever(videoPlayerController.state).doReturn(VideoPlayerController.State.Buffering)
        viewModel.readyToPlay.assertValue(false)
        viewModel.loadingState.assertValue(LoadingState.Loading)

        viewModel.onStateChanged(otherUrlSource, VideoPlayerController.State.Ready)
        viewModel.readyToPlay.assertValue(false)
        viewModel.loadingState.assertValue(LoadingState.Loading)

        viewModel.onStateChanged(otherUrlSource, VideoPlayerController.State.Idle)
        viewModel.readyToPlay.assertValue(true)
        viewModel.loadingState.assertValue(LoadingState.Loading)

        viewModel.onStateChanged(currentUrlSource, VideoPlayerController.State.Ready)
        viewModel.loadingState.assertValue(LoadingState.Ready)
        viewModel.readyToPlay.assertValue(false)

        viewModel.onStateChanged(currentUrlSource, VideoPlayerController.State.Ended)
        viewModel.loadingState.assertValue(LoadingState.Ready)
        viewModel.readyToPlay.assertValue(false)

        viewModel.onStateChanged(currentUrlSource,
            VideoPlayerController.State.Error("error message"))
        viewModel.loadingState.assertValue(LoadingState.Error(ErrorCause.PLAYBACK))
        viewModel.readyToPlay.assertValue(false)
    }
}