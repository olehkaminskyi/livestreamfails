package olehakaminskyi.livestreamfails.local.videos

import olehakaminskyi.livestreamfails.local.videos.entities.LocalVideoPost
import olehakaminskyi.livestreamfails.local.videos.entities.VideoPostsPage
import olehakaminskyi.livestreamfails.local.videos.room.VideoPostsPagesDao
import olehakaminskyi.livestreamfails.local.videos.room.VideoPostsDao

interface LocalVideoPostsDataStore {
    suspend fun getPosts(): List<LocalVideoPost>
    suspend fun putPosts(page: Int, posts: List<LocalVideoPost>)
    suspend fun containsPage(page: Int): Boolean
    suspend fun deleteAll()
}

internal class LocalVideoPostsDataStoreImpl(
    private val _videoPostsDao: VideoPostsDao,
    private val _videoPostsPageDao: VideoPostsPagesDao
) : LocalVideoPostsDataStore {

    override suspend fun containsPage(page: Int) = _videoPostsPageDao.getPageById(page).isNotEmpty()

    override suspend fun getPosts(): List<LocalVideoPost> = _videoPostsDao.getAllPosts()

    override suspend fun putPosts(page: Int, posts: List<LocalVideoPost>) {
        _videoPostsPageDao.insert(VideoPostsPage(page))
        posts.forEach { _videoPostsDao.insert(it) }
    }

    override suspend fun deleteAll() {
        _videoPostsPageDao.deleteAll()
    }
}
