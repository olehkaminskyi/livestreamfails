package olehakaminskyi.livestreamfails.data.videos.injection

import olehakaminskyi.livestreamfails.domain.videos.VideoPostsRepository
import olehakaminskyi.livestreamfails.domain.videos.VideoPostsRepositoryImplementation
import olehakaminskyi.livestreamfails.domain.videos.VideosRepository
import olehakaminskyi.livestreamfails.domain.videos.VideosRepositoryImplementation
import olehakaminskyi.livestreamfails.remote.videos.injection.KoinRemoteVideosModules
import org.koin.dsl.module

val KoinDataVideoModules = KoinRemoteVideosModules + arrayOf(
    module {
        single { VideoPostsRepositoryImplementation(get()) as VideoPostsRepository }
        single { VideosRepositoryImplementation(get()) as VideosRepository }
    }
)
