package olehakaminskyi.livestreamfails.presentation.videos.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import olehakaminskyi.livestreamfails.domain.videos.VideosRepository
import olehakaminskyi.livestreamfails.player.UrlSource
import olehakaminskyi.livestreamfails.player.VideoPlayerController

class VideoItemViewModel(
    private val _videoPlayer: VideoPlayerController,
    private val _videosRepository: VideosRepository,
    private val _videoPostId: Long
) : ViewModel(), VideoPlayerController.Listener {

    private var _dataFetchJob: Job? = null
    private var _isPlayingCurrent = false
    private var _videoSource: UrlSource? = null
    private val _isLoaded get() = _videoSource != null

    private val _loadingStateLiveData = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> get() = _loadingStateLiveData

    private val _readyToPlay = MutableLiveData<Boolean>()
    val readyToPlay: LiveData<Boolean>
        get() {
            if (!_isLoaded) {
                load()
            }
            return _readyToPlay
        }

    private fun load() {
        _loadingStateLiveData.value = LoadingState.Loading
        _dataFetchJob = viewModelScope.launch {
            _videosRepository.getVideoForPost(_videoPostId)
                .success {
                    _videoSource = UrlSource(it.url)
                    _videoPlayer.addListener(this@VideoItemViewModel)
                    _readyToPlay.value = _videoPlayer.state == VideoPlayerController.State.Idle
                }
                .error {
                    _loadingStateLiveData.value = LoadingState.Error(ErrorCause.DATA)
                }
        }
    }

    fun play() {
        _videoSource?.let {
            _loadingStateLiveData.value = LoadingState.Loading
            _videoPlayer.play(it)
        }
    }

    fun stop() {
        _dataFetchJob?.cancel()
        if (_isPlayingCurrent) {
            _videoPlayer.stop()
        }
    }

    public override fun onCleared() {
        super.onCleared()
        _videoPlayer.removeListener(this)
    }

    override fun onStateChanged(urlSource: UrlSource, state: VideoPlayerController.State) {
        _isPlayingCurrent = urlSource == _videoSource
        if (!_isPlayingCurrent) {
            _readyToPlay.value = _isLoaded && state == VideoPlayerController.State.Idle
            return
        }
        _readyToPlay.value = state == VideoPlayerController.State.Idle
        when (state) {
            VideoPlayerController.State.Ready ->
                _loadingStateLiveData.value = LoadingState.Ready
            is VideoPlayerController.State.Error ->
                _loadingStateLiveData.value = LoadingState.Error(ErrorCause.PLAYBACK)
        }
    }

    fun pause() {
        if (_isPlayingCurrent) {
            _videoPlayer.pause()
        }
    }

    fun resume() {
        if (_isPlayingCurrent) {
            _videoPlayer.resume()
        }
    }
}

sealed class LoadingState {
    object Ready : LoadingState()
    object Loading : LoadingState()
    data class Error(val errorCause: ErrorCause) : LoadingState()
}

enum class ErrorCause {
    PLAYBACK, DATA
}