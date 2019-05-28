package olehakaminskyi.livestreamfails.local.videos.room

import androidx.room.Database
import androidx.room.RoomDatabase
import olehakaminskyi.livestreamfails.local.videos.entities.LocalVideo
import olehakaminskyi.livestreamfails.local.videos.entities.LocalVideoPost
import olehakaminskyi.livestreamfails.local.videos.entities.VideoPostsPage

@Database(entities = [VideoPostsPage::class, LocalVideoPost::class, LocalVideo::class], version = 1)
internal abstract class VideosRoomDataBase : RoomDatabase() {
    abstract fun videoPostsDao(): VideoPostsDao
    abstract fun videoPostsPagesDao(): VideoPostsPagesDao
    abstract fun videosDao(): VideosDao
}