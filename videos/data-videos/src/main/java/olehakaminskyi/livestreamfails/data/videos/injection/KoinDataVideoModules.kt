package olehakaminskyi.livestreamfails.data.videos.injection

import olehakaminskyi.livestreamfails.domain.videos.VideoPostsRepository
import olehakaminskyi.livestreamfails.domain.videos.VideoPostsRepositoryImplementation
import olehakaminskyi.livestreamfails.domain.videos.VideosRepository
import olehakaminskyi.livestreamfails.domain.videos.VideosRepositoryImplementation
import olehakaminskyi.livestreamfails.local.videos.injection.KoinLocalVideosModules
import olehakaminskyi.livestreamfails.remote.videos.injection.KoinRemoteVideosModules
import org.koin.dsl.module

val KoinDataVideoModules = KoinRemoteVideosModules + KoinLocalVideosModules + arrayOf(
    module {
        single { VideoPostsRepositoryImplementation(get(), get()) as VideoPostsRepository }
        single { VideosRepositoryImplementation(get(), get()) as VideosRepository }
    }
)
