package olehakaminskyi.livestreamfails.domain.videos

import com.nhaarman.mockitokotlin2.whenever
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.any
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.ErrorType
import olehakaminskyi.livestreamfails.domain.ResultError
import olehakaminskyi.livestreamfails.local.videos.LocalVideoPostsDataStore
import olehakaminskyi.livestreamfails.local.videos.entities.LocalVideoPost
import olehakaminskyi.livestreamfails.remote.videos.RemoteVideoPostsDataSource
import olehakaminskyi.livestreamfails.remote.videos.entities.RemoteVideoPost
import org.junit.Test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class VideoPostsRepositoryImplementationTest {
    lateinit var repository: VideoPostsRepositoryImplementation
    @Mock
    lateinit var local: LocalVideoPostsDataStore
    @Mock
    lateinit var remote: RemoteVideoPostsDataSource

    @Before
    fun setup() {
        repository = VideoPostsRepositoryImplementation(remote, local)
    }

    @Test
    fun refresh_error() = runBlockingTest {
        val page = 0
        val localPost = LocalVideoPost(page = 0, id = 1, thumbnail = "url")
        whenever(remote.getPosts(page))
            .thenReturn(DataResult(error = ResultError(ErrorType.Unknown)))
        whenever(local.getPosts()).thenReturn(listOf(localPost))

        val result = repository.refresh()
        assertFalse(result.isSuccessful)
        assertEquals(ErrorType.Unknown, result.error!!.type)
        result.data!!.forEach {
            assertEquals(it.thumbnail, localPost.thumbnail)
            assertEquals(it.id, localPost.id)
        }
        verify(local, never()).deleteAll()
    }

    @Test
    fun refresh_success() = runBlockingTest {
        val page = 0
        val remotePost = RemoteVideoPost(1, "url")
        val localPost = LocalVideoPost(page = 0, id = 1, thumbnail = "url")
        whenever(local.getPosts()).thenReturn(listOf(localPost))
        whenever(remote.getPosts(page)).thenReturn(DataResult(listOf(remotePost)))

        val result = repository.refresh()
        assertTrue(result.isSuccessful)
        result.data!!.forEach {
            assertEquals(it.thumbnail, remotePost.thumbnail)
            assertEquals(it.id, remotePost.id)
        }

        verify(local, times(1)).deleteAll()
        verify(local, times(1)).putPosts(eq(page), any())
    }

    @Test
    fun getPostIncluding_error() = runBlockingTest {
        val page = 0
        val localPost = LocalVideoPost(page = 0, id = 1, thumbnail = "url")
        whenever(remote.getPosts(page))
            .thenReturn(DataResult(error = ResultError(ErrorType.Unknown)))

        whenever(local.containsPage(page)).thenReturn(false)
        whenever(local.getPosts()).thenReturn(listOf(localPost))

        val result = repository.getPostIncluding(0)
        assertFalse(result.isSuccessful)
        assertEquals(ErrorType.Unknown, result.error!!.type)
        result.data!!.forEach {
            assertEquals(it.thumbnail, localPost.thumbnail)
            assertEquals(it.id, localPost.id)
        }
    }

    @Test
    fun getPostIncluding_success() = runBlockingTest {
        val page = 1
        val remotePost = RemoteVideoPost(1, "url")
        val localPost = LocalVideoPost(page = 0, id = 1, thumbnail = "url")

        whenever(local.containsPage(page)).thenReturn(false)
        whenever(local.getPosts()).thenReturn(listOf(localPost))
        whenever(remote.getPosts(page)).thenReturn(DataResult(listOf(remotePost)))

        val result = repository.getPostIncluding(page)
        assertTrue(result.isSuccessful)
        result.data!!.forEach {
            assertEquals(it.thumbnail, remotePost.thumbnail)
            assertEquals(it.id, remotePost.id)
        }

        verify(local, times(1)).putPosts(eq(page), any())
    }

    @Test
    fun getPostIncluding_from_cache() = runBlockingTest {
        val page = 1
        val localPost = LocalVideoPost(page = 0, id = 1, thumbnail = "url")

        whenever(local.containsPage(page)).thenReturn(true)
        whenever(local.getPosts()).thenReturn(listOf(localPost))

        val result = repository.getPostIncluding(page)
        assertTrue(result.isSuccessful)
        result.data!!.forEach {
            assertEquals(it.thumbnail, localPost.thumbnail)
            assertEquals(it.id, localPost.id)
        }
    }
}