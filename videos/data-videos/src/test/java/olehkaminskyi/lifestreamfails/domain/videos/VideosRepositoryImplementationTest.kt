package olehkaminskyi.lifestreamfails.domain.videos

import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.ErrorType
import olehakaminskyi.livestreamfails.domain.ResultError
import olehakaminskyi.livestreamfails.domain.videos.VideosRepositoryImplementation
import olehakaminskyi.livestreamfails.domain.videos.entities.Video
import olehakaminskyi.livestreamfails.local.videos.LocalVideosDataStore
import olehakaminskyi.livestreamfails.local.videos.entities.LocalVideo
import olehakaminskyi.livestreamfails.remote.videos.RemoteVideosDataSource
import olehakaminskyi.livestreamfails.remote.videos.entities.RemoteVideo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class VideosRepositoryImplementationTest {

    val postId = 10L
    val videUrl = "videoUrl"

    lateinit var repository: VideosRepositoryImplementation

    @Mock
    lateinit var localDataStore: LocalVideosDataStore
    @Mock
    lateinit var remoteDataSource: RemoteVideosDataSource

    @Before
    fun setup() {
        repository = VideosRepositoryImplementation(remoteDataSource, localDataStore)
    }

    @Test
    fun getVideoForPost_repository_empty_error() = runBlockingTest {
        whenever(localDataStore.getVideo(postId)).thenReturn(null)
        whenever(remoteDataSource.getVideoByPostId(postId))
            .thenReturn(DataResult(error = ResultError(ErrorType.Unknown, null)))
        val result = repository.getVideoForPost(postId)
        assertFalse(result.isSuccessful)
        assertEquals(null, result.data)
        assertEquals(result.error!!.type, ErrorType.Unknown)
    }

    @Test
    fun getVideoForPost_repository_loaded() = runBlockingTest {
        whenever(localDataStore.getVideo(postId))
            .thenReturn(LocalVideo(postId, videUrl, 0))
        val result = repository.getVideoForPost(postId)
        assertTrue(result.isSuccessful)
        assertEquals(Video(videUrl), result.data)
        assertEquals(result.error, null)
    }

    @Test
    fun getVideoForPost_success() = runBlockingTest {
        whenever(localDataStore.getVideo(postId)).thenReturn(null)
        whenever(remoteDataSource.getVideoByPostId(postId))
            .thenReturn(DataResult(data = RemoteVideo(videUrl)))
        val result = repository.getVideoForPost(postId)
        assertTrue(result.isSuccessful)
        assertEquals(Video(videUrl), result.data)
        assertEquals(result.error, null)
    }
}