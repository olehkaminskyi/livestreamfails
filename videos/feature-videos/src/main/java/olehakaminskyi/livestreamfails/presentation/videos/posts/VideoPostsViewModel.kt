package olehakaminskyi.livestreamfails.presentation.videos.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import olehakaminskyi.livestreamfails.domain.DataResult
import olehakaminskyi.livestreamfails.domain.videos.VideoPostsRepository
import olehakaminskyi.livestreamfails.presentation.videos.uimodels.UiVideoPost
import olehakaminskyi.livestreamfails.presentation.videos.uimodels.toUi

class VideoPostsViewModel(
    private val _videoPostsRepository: VideoPostsRepository
) : ViewModel() {
    private var fetchJob: Job? = null
    private var _currentPage = 0
    private val _postsLiveData = MutableLiveData<DataResult<List<UiVideoPost>>>()
    val posts: LiveData<DataResult<List<UiVideoPost>>> get() = _postsLiveData

    private val _progressLiveData = MutableLiveData<Progress>()
    val progress: LiveData<Progress> get() = _progressLiveData

    init {
        load(0)
    }

    fun loadMore() {
        load(_currentPage + 1)
    }

    fun refresh() {
        if (fetchJob?.isActive == true) {
            return
        }
        _progressLiveData.value = Progress.LOADING

        fetchJob = viewModelScope.launch {
            _postsLiveData.value = _videoPostsRepository.refresh()
                .map { list -> list.map { it.toUi() } }
                .also {
                    if (it.isSuccessful) {
                        _currentPage = 0
                    }
                }
            _progressLiveData.value = Progress.NONE
        }
    }

    fun load(page: Int) {
        if (fetchJob?.isActive == true) {
            return
        }
        _progressLiveData.value = if (page == 0) {
            Progress.LOADING
        } else {
            Progress.LOADING_MORE
        }

        fetchJob = viewModelScope.launch {
            _postsLiveData.value = _videoPostsRepository.getPostIncluding(page)
                .map { list -> list.map { it.toUi() } }
                .also {
                    if (it.isSuccessful) {
                        _currentPage = page
                    }
                }
            _progressLiveData.value = Progress.NONE
        }
    }
}

enum class Progress {
    LOADING, LOADING_MORE, NONE
}