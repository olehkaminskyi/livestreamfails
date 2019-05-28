package olehakaminskyi.livestreamfails.local.videos.injection

import androidx.room.Room
import olehakaminskyi.livestreamfails.local.videos.LocalVideoPostsDataStore
import olehakaminskyi.livestreamfails.local.videos.LocalVideoPostsDataStoreImpl
import olehakaminskyi.livestreamfails.local.videos.LocalVideosDataStore
import olehakaminskyi.livestreamfails.local.videos.LocalVideosDataStoreImpl
import olehakaminskyi.livestreamfails.local.videos.room.VideosRoomDataBase
import org.koin.dsl.module

val KoinLocalVideosModules = arrayOf(
    module {
        single {
            Room.databaseBuilder(
                get(),
                VideosRoomDataBase::class.java,
                "VideosFeature"
            ).build()
        }
        factory { get<VideosRoomDataBase>().videoPostsDao() }
        factory { get<VideosRoomDataBase>().videoPostsPagesDao() }
        factory { get<VideosRoomDataBase>().videosDao() }
        single { LocalVideoPostsDataStoreImpl(get(), get()) as LocalVideoPostsDataStore }
        single { LocalVideosDataStoreImpl(get()) as LocalVideosDataStore }
    }
)