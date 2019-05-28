package olehakaminskyi.livestreamfails.remote.videos

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.ErrorType
import olehakaminskyi.livestreamfails.domain.ResultError
import olehakaminskyi.livestreamfails.remote.BaseRemoteDataSource
import olehakaminskyi.livestreamfails.remote.videos.entities.RemoteVideo
import org.jsoup.Jsoup
import java.io.IOException

interface RemoteVideosDataSource {
    suspend fun getVideoByPostId(postId: Long): DataResult<RemoteVideo>
}

internal class RemoteVideosDataSourceImpl : RemoteVideosDataSource {
    override suspend fun getVideoByPostId(postId: Long): DataResult<RemoteVideo> =
        withContext(Dispatchers.IO) {
            val url = "${BaseRemoteDataSource.LIVESTREAMFAILS_URL}post/$postId"
            try {
                Jsoup.connect(url).timeout(BaseRemoteDataSource.TIMEOUT.toInt()).get()
                    .select("#video-$postId source")?.attr("src")
                    .let {
                        if (it.isNullOrBlank()) {
                            DataResult(error = ResultError(ErrorType.Unknown))
                        } else {
                            DataResult(RemoteVideo(it))
                        }
                    }
            } catch (e: IOException) {
                DataResult<RemoteVideo>(error = ResultError(ErrorType.NoConnection, e))
            }
        }
}