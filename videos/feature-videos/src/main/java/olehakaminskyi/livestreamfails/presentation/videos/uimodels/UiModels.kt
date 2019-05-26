package olehakaminskyi.livestreamfails.presentation.videos.uimodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import olehakaminskyi.livestreamfails.domain.videos.entities.VideoPost

@Parcelize
data class UiVideoPost(val id: Long, val thumbnail: String) : Parcelable {
    fun toDomain() = VideoPost(id, thumbnail)
}

fun VideoPost.toUi() = this.run { UiVideoPost(id, thumbnail) }