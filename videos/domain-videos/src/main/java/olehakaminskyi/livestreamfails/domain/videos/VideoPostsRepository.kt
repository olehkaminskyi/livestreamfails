package olehakaminskyi.livestreamfails.domain.videos

import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.videos.entities.VideoPost

interface VideoPostsRepository {
    suspend fun refresh(): DataResult<List<VideoPost>>
    suspend fun getPostIncluding(page: Int): DataResult<List<VideoPost>>
}