package olehakaminskyi.livestreamfails.local.videos

import olehakaminskyi.livestreamfails.local.videos.entities.LocalVideo
import olehakaminskyi.livestreamfails.local.videos.room.VideosDao

interface LocalVideosDataStore {
    suspend fun putVideo(video: LocalVideo)
    suspend fun getVideo(postId: Long): LocalVideo?
}

internal class LocalVideosDataStoreImpl(private val _videosDao: VideosDao) : LocalVideosDataStore {
    override suspend fun putVideo(video: LocalVideo) = _videosDao.insert(video)

    override suspend fun getVideo(postId: Long): LocalVideo? = _videosDao.getById(postId)
}