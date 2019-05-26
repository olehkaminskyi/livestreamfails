package olehakaminskyi.livestreamfails.domain.videos

import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.videos.entities.Video

class VideosRepositoryImplementation : VideosRepository {

    override suspend fun getVideoForPost(videoPostId: Long): DataResult<Video> = DataResult(
        Video(testUrls[((videoPostId - 1) % testUrls.size).toInt()])
    )

    companion object {
        val testUrls = arrayOf(
            "https://stream.livestreamfails.com/video/5ce21b4041b4e.mp4",
            "https://stream.livestreamfails.com/video/5ce47e07697a3.mp4",
            "https://stream.livestreamfails.com/video/5ce468b36ee08.mp4"
        )
    }
}