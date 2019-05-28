package olehakaminskyi.livestreamfails.remote.videos.injection

import olehakaminskyi.livestreamfails.remote.videos.RemoteVideosDataSource
import olehakaminskyi.livestreamfails.remote.videos.RemoteVideosDataSourceImpl
import olehakaminskyi.livestreamfails.remote.videos.RemoteVideoPostsDataSource
import olehakaminskyi.livestreamfails.remote.videos.RemoteVideoPostsDataSourceImpl
import org.koin.dsl.module

val KoinRemoteVideosModules = arrayOf(
    module {
        single { RemoteVideosDataSourceImpl() as RemoteVideosDataSource }
        single { RemoteVideoPostsDataSourceImpl() as RemoteVideoPostsDataSource }
    }
)