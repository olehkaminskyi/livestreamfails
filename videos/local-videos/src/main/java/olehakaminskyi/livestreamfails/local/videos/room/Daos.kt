package olehakaminskyi.livestreamfails.local.videos.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import olehakaminskyi.livestreamfails.local.videos.entities.LocalVideo
import olehakaminskyi.livestreamfails.local.videos.entities.LocalVideoPost
import olehakaminskyi.livestreamfails.local.videos.entities.VideoPostsPage

@Dao
internal interface VideoPostsDao {

    @Query("SELECT * from video_posts_table")
    suspend fun getAllPosts(): List<LocalVideoPost>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(videoPost: LocalVideoPost)
}

@Dao
internal interface VideoPostsPagesDao {
    @Query("SELECT * from page_table WHERE page=:page")
    suspend fun getPageById(page: Int): List<VideoPostsPage>

    @Query("DELETE FROM page_table")
    suspend fun deleteAll()

    @Insert
    suspend fun insert(videoPost: VideoPostsPage)
}

@Dao
internal interface VideosDao {
    @Query("SELECT * from videos_table WHERE postId=:postId")
    suspend fun getById(postId: Long): LocalVideo?

    @Insert
    suspend fun insert(videoPost: LocalVideo)
}