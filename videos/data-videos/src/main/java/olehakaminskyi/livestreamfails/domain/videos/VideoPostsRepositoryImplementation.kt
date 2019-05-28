package olehakaminskyi.livestreamfails.domain.videos

import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.videos.entities.VideoPost
import olehakaminskyi.livestreamfails.local.videos.LocalVideoPostsDataStore
import olehakaminskyi.livestreamfails.local.videos.entities.LocalVideoPost
import olehakaminskyi.livestreamfails.remote.videos.RemoteVideoPostsDataSource
import olehakaminskyi.livestreamfails.remote.videos.entities.RemoteVideoPost

class VideoPostsRepositoryImplementation(
    private val _remoteDataSource: RemoteVideoPostsDataSource,
    private val _localDataStore: LocalVideoPostsDataStore
) : VideoPostsRepository {

    override suspend fun refresh(): DataResult<List<VideoPost>> {
        val page = 0
        val result = _remoteDataSource.getPosts(page)
        if (result.isSuccessful) {
            result.data?.let { data ->
                _localDataStore.deleteAll()
                _localDataStore.putPosts(page, data.map { it.toLocal(page) })
            }
        }
        return DataResult(data = _localDataStore.getPosts()
            .map { it.toDomain() }, error = result.error)
    }

    override suspend fun getPostIncluding(page: Int): DataResult<List<VideoPost>> =
        if (_localDataStore.containsPage(page)) {
            DataResult(_localDataStore.getPosts().map { it.toDomain() })
        } else {
            _remoteDataSource.getPosts(page)
                .map { list -> list.map { it.toLocal(page) } }
                .apply { data?.let { _localDataStore.putPosts(page, it) } }
                .run {
                    DataResult(_localDataStore.getPosts().map { it.toDomain() }, error = error)
                }
        }

    private fun RemoteVideoPost.toLocal(page: Int) = LocalVideoPost(id, page, thumbnail)
    private fun LocalVideoPost.toDomain() = VideoPost(id, thumbnail)
}