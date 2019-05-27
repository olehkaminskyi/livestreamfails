package olehakaminskyi.livestreamfails.remote.videos

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.ErrorType
import olehakaminskyi.livestreamfails.domain.ResultError
import olehakaminskyi.livestreamfails.remote.BaseRemoteDataSource
import olehakaminskyi.livestreamfails.remote.videos.entities.RemoteVideoPost
import org.jsoup.Jsoup
import java.io.IOException

interface RemoteVideoPostsDataSource {
    suspend fun getPosts(page: Int): DataResult<List<RemoteVideoPost>>
}

internal class RemoteVideoPostsDataSourceImpl : BaseRemoteDataSource(), RemoteVideoPostsDataSource {
    override suspend fun getPosts(page: Int): DataResult<List<RemoteVideoPost>> =
        withContext(Dispatchers.IO) {
            val url = "${LIVESTREAMFAILS_URL}load/loadPosts.php?" +
                    "loadPostPage=$page" +
                    "&loadPostMode=standard" +
                    "&loadPostOrder=new" +
                    "&loadPostTimeFrame=year" +
                    "&loadPostNSFW=0"
            try {
                Jsoup.connect(url).timeout(TIMEOUT.toInt()).get()
                    .select(".card.col-sm-3.post-card")
                    .map { element ->
                        val aHref = element.selectFirst("a").attributes()["href"] ?: ""
                        val imgSrc = element.selectFirst("img.card-img-top").attributes()["src"]
                        RemoteVideoPost(aHref.split('/').lastOrNull()?.toLong() ?: 0, imgSrc)
                    }
                    .let {
                        if (it.isEmpty()) {
                            DataResult(ResultError(ErrorType.Unknown))
                        } else {
                            DataResult(it)
                        }
                    }
            } catch (e: IOException) {
                DataResult<List<RemoteVideoPost>>(ResultError(ErrorType.NoConnection, e))
            }
        }
}