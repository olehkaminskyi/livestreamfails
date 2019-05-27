package olehakaminskyi.livestreamfails.domain.videos

import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.videos.entities.VideoPost
import olehakaminskyi.livestreamfails.remote.videos.RemoteVideoPostsDataSource
import olehakaminskyi.livestreamfails.remote.videos.entities.RemoteVideoPost

class VideoPostsRepositoryImplementation(
    private val _remoteDataSource: RemoteVideoPostsDataSource
) : VideoPostsRepository {

    override suspend fun getPostIncluding(page: Int): DataResult<List<VideoPost>> =
        _remoteDataSource.getPosts(page).map { list -> list.map { it.toDomain() } }

    private fun RemoteVideoPost.toDomain() = VideoPost(id, thumbnail)
}