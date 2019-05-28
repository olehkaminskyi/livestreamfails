package olehakaminskyi.livestreamfails.local.videos

import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import olehakaminskyi.livestreamfails.local.videos.entities.LocalVideo
import olehakaminskyi.livestreamfails.local.videos.room.VideosDao
import org.junit.Test

import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@UseExperimental(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LocalVideosDataStoreImplTest {

    @Mock
    internal lateinit var videoDao: VideosDao
    internal lateinit var localVideoDataStore: LocalVideosDataStoreImpl

    @Before
    fun setup() {
        localVideoDataStore = LocalVideosDataStoreImpl(videoDao)
    }

    @Test
    fun putVideo() = runBlockingTest {
        val video = LocalVideo(1, "url")
        localVideoDataStore.putVideo(video)
    }

    @Test
    fun getVideo() = runBlockingTest {
        val id = 1L
        val video = LocalVideo(id, "url")
        whenever(videoDao.getById(id)).thenReturn(video)
        val result = localVideoDataStore.getVideo(id)
        assertEquals(video, result)
    }
}