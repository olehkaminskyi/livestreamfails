package olehakaminskyi.livestreamfails.presentation.videos.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.videos.VideoPostsRepository
import olehakaminskyi.livestreamfails.player.VideoPlayerController
import olehakaminskyi.livestreamfails.presentation.videos.uimodels.UiVideoPost
import olehakaminskyi.livestreamfails.presentation.videos.uimodels.toUi

class VideoPostsViewModel(
    private val _videoPostsRepository: VideoPostsRepository,
    private val _videoController: VideoPlayerController
) : ViewModel() {

    private val _postsLiveData = MutableLiveData<DataResult<List<UiVideoPost>>>()
    val posts: LiveData<DataResult<List<UiVideoPost>>> get() = _postsLiveData

    private val _progressLiveData = MutableLiveData<Progress>()
    val progress: LiveData<Progress> get() = _progressLiveData

    init {
        load(0)
    }

    fun load(page: Int) {
        _progressLiveData.value = if (page == 0) {
            Progress.LOADING
        } else {
            Progress.LOADING_MORE
        }
        viewModelScope.launch {
            _postsLiveData.value = _videoPostsRepository.getPostIncluding(page)
                .map { list -> list.map { it.toUi() } }
            _progressLiveData.value = Progress.NONE
        }
    }

    fun pause() {
        _videoController.pause()
    }

    fun resume() {
        _videoController.resume()
    }
}

enum class Progress {
    LOADING, LOADING_MORE, NONE
}