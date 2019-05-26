package olehakaminskyi.livestreamfails.presentation.videos.posts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.videos.VideoPostsRepository
import olehakaminskyi.livestreamfails.domain.videos.entities.VideoPost
import olehakaminskyi.livestreamfails.player.VideoPlayerController
import olehakaminskyi.livestreamfails.tests.CoroutinesTestRule
import olehakaminskyi.livestreamfails.tests.assertThat
import olehakaminskyi.livestreamfails.tests.valuesVerifier
import org.junit.Before
import org.junit.Rule
import org.junit.Test

import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class VideoPostsViewModelTest {
    @get:Rule
    val instantTaskTestRule = InstantTaskExecutorRule()
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    private lateinit var viewModel: VideoPostsViewModel
    @Mock
    lateinit var videoController: VideoPlayerController
    @Mock
    lateinit var videoPostsRepository: VideoPostsRepository

    @Before
    fun setup() = coroutinesTestRule.testDispatcher.runBlockingTest {
        whenever(videoPostsRepository.getPostIncluding(any()))
            .doReturn(mock { DataResult::class.java })
        viewModel = VideoPostsViewModel(videoPostsRepository, videoController)
    }

    @Test
    fun load() = runBlockingTest {
        val result = listOf(VideoPost(1, "url"))
        whenever(videoPostsRepository.getPostIncluding(0)).doReturn(DataResult(result))
        viewModel.load(0)
        viewModel.posts.assertThat { dataResult ->
            dataResult.data?.let {
                it.size == result.size &&
                        it.size == 1 &&
                        it[0].id == result[0].id
            } ?: false
        }
    }

    @Test
    fun pause() {
        viewModel.pause()
        verify(videoController, times(1)).pause()
    }

    @Test
    fun resume() {
        viewModel.resume()
        verify(videoController, times(1)).resume()
    }

    @Test
    fun getProgress() = runBlockingTest {
        val valuesVerifier = viewModel.progress.valuesVerifier(
            Progress.NONE, Progress.LOADING, Progress.NONE,
            Progress.LOADING_MORE, Progress.NONE
        )
        viewModel.load(0)
        viewModel.load(1)
        valuesVerifier.verify()
    }
}