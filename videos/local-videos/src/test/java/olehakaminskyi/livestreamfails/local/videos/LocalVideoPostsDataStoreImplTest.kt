package olehakaminskyi.livestreamfails.local.videos

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import olehakaminskyi.livestreamfails.local.videos.entities.LocalVideoPost
import olehakaminskyi.livestreamfails.local.videos.entities.VideoPostsPage
import olehakaminskyi.livestreamfails.local.videos.room.VideoPostsDao
import olehakaminskyi.livestreamfails.local.videos.room.VideoPostsPagesDao
import org.junit.Test

import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LocalVideoPostsDataStoreImplTest {

    internal lateinit var localPostsDataStore: LocalVideoPostsDataStoreImpl

    @Mock
    internal lateinit var pagePostsDao: VideoPostsPagesDao
    @Mock
    internal lateinit var videoPostsDao: VideoPostsDao

    @Before
    fun setup() {
        localPostsDataStore = LocalVideoPostsDataStoreImpl(videoPostsDao, pagePostsDao)
    }

    @Test
    fun containsPage() = runBlockingTest {
        val page = 1
        whenever(pagePostsDao.getPageById(page)).thenReturn(listOf(VideoPostsPage(page)))
        assertTrue(localPostsDataStore.containsPage(page))
        whenever(pagePostsDao.getPageById(page)).thenReturn(emptyList())
        assertFalse(localPostsDataStore.containsPage(page))
    }

    @Test
    fun getPosts() = runBlockingTest {
        val result = listOf(LocalVideoPost(1, 0, "thumbnail"))
        whenever(videoPostsDao.getAllPosts()).thenReturn(result)
        assertEquals(result, localPostsDataStore.getPosts())
    }

    @Test
    fun putPosts() = runBlockingTest {
        val post = LocalVideoPost(1, 0, "thumbnail")
        val posts = listOf(post)
        val page = 1
        localPostsDataStore.putPosts(page, posts)
        verify(videoPostsDao, times(1)).insert(post)
        verify(pagePostsDao, times(1)).insert(any())
    }

    @Test
    fun deleteAll() = runBlockingTest {
        localPostsDataStore.deleteAll()
        verify(pagePostsDao, times(1)).deleteAll()
    }
}