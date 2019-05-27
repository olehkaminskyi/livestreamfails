package olehakaminskyi.livestreamfails.domain.videos

import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.videos.entities.Video
import olehakaminskyi.livestreamfails.remote.videos.RemoteVideosDataSource
import olehakaminskyi.livestreamfails.remote.videos.entities.RemoteVideo

class VideosRepositoryImplementation(
    private val _remoteDataSource: RemoteVideosDataSource
) : VideosRepository {

    override suspend fun getVideoForPost(videoPostId: Long): DataResult<Video> =
        _remoteDataSource.getVideoUrlByPostId(videoPostId).map { it.toDomain() }

    private fun RemoteVideo.toDomain() = Video(url)
}