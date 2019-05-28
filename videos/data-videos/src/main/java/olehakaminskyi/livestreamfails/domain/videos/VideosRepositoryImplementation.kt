package olehakaminskyi.livestreamfails.domain.videos

import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.videos.entities.Video
import olehakaminskyi.livestreamfails.local.videos.LocalVideosDataStore
import olehakaminskyi.livestreamfails.local.videos.entities.LocalVideo
import olehakaminskyi.livestreamfails.remote.videos.RemoteVideosDataSource
import olehakaminskyi.livestreamfails.remote.videos.entities.RemoteVideo

class VideosRepositoryImplementation(
    private val _remoteDataSource: RemoteVideosDataSource,
    private val _localDataStore: LocalVideosDataStore
) : VideosRepository {

    override suspend fun getVideoForPost(videoPostId: Long): DataResult<Video> =
        _localDataStore.getVideo(videoPostId)?.let { DataResult(it.toDomain()) }
            ?: _remoteDataSource.getVideoByPostId(videoPostId)
                .also { result -> result.data?.let {
                    _localDataStore.putVideo(it.toLocal(videoPostId)) }
                }.map { it.toDomain() }

    private fun RemoteVideo.toDomain() = Video(url)
    private fun RemoteVideo.toLocal(postId: Long) = LocalVideo(postId, url)
    private fun LocalVideo.toDomain() = Video(url)
}