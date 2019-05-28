package olehakaminskyi.livestreamfails.presentation.videos.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.Player
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_video_item.*
import olehakaminskyi.livestreamfails.presentation.videos.BaseVideosFeatureFragment
import olehakaminskyi.livestreamfails.presentation.videos.R
import olehakaminskyi.livestreamfails.presentation.videos.uimodels.UiVideoPost
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class VideoItemFragment : BaseVideosFeatureFragment(), View.OnAttachStateChangeListener {

    private val _videoPost: UiVideoPost by lazy { arguments!!.getParcelable(POST) as UiVideoPost }
    private val _viewModel: VideoItemViewModel by viewModel { parametersOf(_videoPost.id) }
    private val _videoPlayer: Player by inject()
    private val _loadingStateChangeObserver: Observer<LoadingState> = Observer {
        when (it) {
        LoadingState.Loading -> {
            videoPlayerView.alpha = 0F
            videoThumbnail.visibility = VISIBLE
            progress.visibility = VISIBLE
            errorText.visibility = INVISIBLE
        }
        LoadingState.Ready -> {
            videoPlayerView.alpha = 1F
            videoThumbnail.visibility = INVISIBLE
            progress.visibility = INVISIBLE
            errorText.visibility = INVISIBLE
        }
        is LoadingState.Error -> {
            videoPlayerView.alpha = 0F
            videoThumbnail.visibility = VISIBLE
            progress.visibility = INVISIBLE
            errorText.visibility = VISIBLE
        }
    } }
    private val _readyToPlayObserver: Observer<Boolean> = Observer { if (it) _viewModel.play() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Picasso.get().load(_videoPost.thumbnail).into(videoThumbnail)
        videoPlayerView.player = null
        view.addOnAttachStateChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        _viewModel.pause()
    }

    override fun onResume() {
        super.onResume()
        _viewModel.resume()
    }

    override fun onViewDetachedFromWindow(v: View?) {
        videoPlayerView.player = null
        _viewModel.loadingState.removeObserver(_loadingStateChangeObserver)
        _viewModel.readyToPlay.removeObserver(_readyToPlayObserver)
        _viewModel.stop()
    }

    override fun onViewAttachedToWindow(v: View?) {
        _viewModel.loadingState.observe(viewLifecycleOwner, _loadingStateChangeObserver)
        _viewModel.readyToPlay.observe(viewLifecycleOwner, _readyToPlayObserver)
        videoPlayerView.player = _videoPlayer
    }

    override fun onDestroyView() {
        super.onDestroyView()
        view?.removeOnAttachStateChangeListener(this)
    }

    companion object {
        private const val POST = "post"
        fun createInstance(post: UiVideoPost) = VideoItemFragment().apply {
            arguments = Bundle().apply { putParcelable(POST, post) }
        }
    }
}