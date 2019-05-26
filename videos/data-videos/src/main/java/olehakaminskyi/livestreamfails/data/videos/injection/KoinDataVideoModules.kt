package olehakaminskyi.livestreamfails.data.videos.injection

import olehakaminskyi.livestreamfails.domain.videos.VideoPostsRepository
import olehakaminskyi.livestreamfails.domain.videos.VideoPostsRepositoryImplementation
import olehakaminskyi.livestreamfails.domain.videos.VideosRepository
import olehakaminskyi.livestreamfails.domain.videos.VideosRepositoryImplementation
import org.koin.dsl.module

val KoinDataVideoModules = arrayOf(
    module {
        single { VideoPostsRepositoryImplementation() as VideoPostsRepository }
        single { VideosRepositoryImplementation() as VideosRepository }
    }
)
