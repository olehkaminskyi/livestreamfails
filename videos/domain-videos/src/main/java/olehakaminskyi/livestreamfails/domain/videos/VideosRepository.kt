package olehakaminskyi.livestreamfails.domain.videos

import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.videos.entities.Video

interface VideosRepository {
    suspend fun getVideoForPost(videoPostId: Long): DataResult<Video>
}