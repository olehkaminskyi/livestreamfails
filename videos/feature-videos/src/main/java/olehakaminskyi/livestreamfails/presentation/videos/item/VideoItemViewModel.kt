package olehakaminskyi.livestreamfails.presentation.videos.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import olehakaminskyi.livestreamfails.domain.videos.VideosRepository
import olehakaminskyi.livestreamfails.player.UrlSource
import olehakaminskyi.livestreamfails.player.VideoPlayerController

class VideoItemViewModel(
    private val _videoPlayer: VideoPlayerController,
    private val _videosRepository: VideosRepository,
    private val _videoPostId: Long
) : ViewModel(), VideoPlayerController.Listener {

    private var _videoSource: UrlSource? = null
    private val isLoaded get() = _videoSource != null

    private val _loadingStateLiveData = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> get() = _loadingStateLiveData

    private var _readyToPlay = MutableLiveData<Boolean>()
    val readyToPlay: LiveData<Boolean>
        get() {
            if (!isLoaded) {
                load()
            }
            return _readyToPlay
        }

    private fun load() {
        _loadingStateLiveData.value = LoadingState.Loading
        viewModelScope.launch {
            _videosRepository.getVideoForPost(_videoPostId)
                .success {
                    _videoSource = UrlSource(it.url)
                    _videoPlayer.addListener(this@VideoItemViewModel)
                    _readyToPlay.value = _videoPlayer.state == VideoPlayerController.State.Idle
                }
                .error { _loadingStateLiveData.value = LoadingState.Error(ErrorCause.DATA) }
        }
    }

    fun play() {
        _videoSource?.let {
            _videoPlayer.play(it)
        }
    }

    fun stop() {
        _videoPlayer.stop()
    }

    public override fun onCleared() {
        super.onCleared()
        _videoPlayer.removeListener(this)
    }

    override fun onStateChanged(urlSource: UrlSource, state: VideoPlayerController.State) {
        if (urlSource != _videoSource) {
            _readyToPlay.value = isLoaded && state == VideoPlayerController.State.Idle
            return
        }
        _readyToPlay.value = state == VideoPlayerController.State.Idle
        _loadingStateLiveData.value = when (state) {
            VideoPlayerController.State.Idle -> LoadingState.Loading
            VideoPlayerController.State.Buffering -> LoadingState.Buffering
            is VideoPlayerController.State.Error -> LoadingState.Error(ErrorCause.PLAYBACK)
            else -> LoadingState.Ready
        }
    }
}

sealed class LoadingState {
    object Buffering : LoadingState()
    object Ready : LoadingState()
    object Loading : LoadingState()
    data class Error(val errorCause: ErrorCause) : LoadingState()
}

enum class ErrorCause {
    PLAYBACK, DATA
}