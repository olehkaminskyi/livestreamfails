package olehakaminskyi.livestreamfails.domain.videos

import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.videos.entities.VideoPost

class VideoPostsRepositoryImplementation : VideoPostsRepository {

    override suspend fun getPostIncluding(page: Int): DataResult<List<VideoPost>> = (1..10)
        .map { VideoPost(it.toLong(), testUrls[it % testUrls.size]) }
        .toList()
        .let { DataResult(it) }

    companion object {
        val testUrls = arrayOf(
            "https://cdn.livestreamfails.com/thumbnail/5ce8c05c73618.jpg",
            "https://cdn.livestreamfails.com/thumbnail/5ce87f761da0a.jpg",
            "https://cdn.livestreamfails.com/thumbnail/5ce8b40b88311.jpg"
        )
    }
}