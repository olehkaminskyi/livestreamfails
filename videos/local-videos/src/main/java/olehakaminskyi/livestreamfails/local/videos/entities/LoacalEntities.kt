package olehakaminskyi.livestreamfails.local.videos.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "video_posts_table",
    foreignKeys = [
        ForeignKey(
            entity = VideoPostsPage::class,
            parentColumns = ["page"],
            childColumns = ["page"],
            onDelete = CASCADE
        )
    ]
)

class LocalVideoPost(
    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "page") val page: Int,
    @ColumnInfo(name = "thumbnail") val thumbnail: String,
    @ColumnInfo(name = "timeStamp") val timeStamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "videos_table",
    foreignKeys = [
        ForeignKey(entity = LocalVideoPost::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = CASCADE)
    ]
)

class LocalVideo(
    @ColumnInfo(name = "postId") val postId: Long,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "timeStamp") val timeStamp: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    val id: Long = 0
)

@Entity(tableName = "page_table")
class VideoPostsPage(
    @PrimaryKey @ColumnInfo(name = "page") val page: Int
)