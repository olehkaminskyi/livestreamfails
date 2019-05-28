package olehakaminskyi.livestreamfails.presentation.videos.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import olehakaminskyi.livestreamfails.domain.ResultError
import olehakaminskyi.livestreamfails.presentation.videos.BaseVideosFeatureFragment
import olehakaminskyi.livestreamfails.presentation.videos.item.VideoItemFragment
import olehakaminskyi.livestreamfails.presentation.videos.uimodels.UiVideoPost
import org.koin.android.viewmodel.ext.android.viewModel
import androidx.recyclerview.widget.DiffUtil.calculateDiff
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_videos.*
import olehakaminskyi.livestreamfails.domain.ErrorType
import olehakaminskyi.livestreamfails.presentation.videos.R

class VideoPostsFragment : BaseVideosFeatureFragment() {

    private val _viewModel: VideoPostsViewModel by viewModel()
    private val _adapter by lazy { PagerAdapter(this) }
    private var _pageChangeCallback: ViewPager2.OnPageChangeCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_videos,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoPostsViewPager.adapter = _adapter
        applyPageChangeCallBack()
        swipeRefreshLayout.setOnRefreshListener { refresh() }
        observeViewModel()
    }

    private fun observeViewModel() {
        _viewModel.progress.observe(viewLifecycleOwner, Observer {
            when (it) {
                Progress.LOADING -> swipeRefreshLayout.isRefreshing = true
                else -> swipeRefreshLayout.isRefreshing = false
            }
        })
        _viewModel.posts.observe(viewLifecycleOwner, Observer { result ->
            result.error { handleError(it) }.success { _adapter.items = it }
        })
    }

    private fun refresh() {
        resetPageChangeCallBack()
        _viewModel.refresh()
    }

    private fun applyPageChangeCallBack() {
        _pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position + PREFETCH_OFFSET > _adapter.itemCount) {
                    _viewModel.loadMore()
                }
            }
        }.also { videoPostsViewPager.registerOnPageChangeCallback(it) }
    }

    private fun removePageChangeCallBack() {
        _pageChangeCallback?.let {
            videoPostsViewPager.unregisterOnPageChangeCallback(it)
            _pageChangeCallback = null
        }
    }

    private fun resetPageChangeCallBack() {
        removePageChangeCallBack()
        applyPageChangeCallBack()
    }

    private fun handleError(errorResult: ResultError) {
        removePageChangeCallBack()

        val stringRes = when (errorResult.type) {
            ErrorType.Unknown -> R.string.error
            ErrorType.NoConnection -> R.string.no_connection_error
            ErrorType.NoData -> R.string.no_data
        }
        view?.let {
            Snackbar.make(it, stringRes, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.refresh) { refresh() }.show()
        }
    }

    class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        var items: List<UiVideoPost> = emptyList()
            set(value) {
                notifyDataSetChanged()
                val old = field
                calculateDiff(object : DiffUtil.Callback() {
                    override fun areItemsTheSame(
                        oldItemPosition: Int,
                        newItemPosition: Int
                    ): Boolean = old[oldItemPosition].id == value[newItemPosition].id

                    override fun getOldListSize(): Int = old.size

                    override fun getNewListSize(): Int = value.size

                    override fun areContentsTheSame(
                        oldItemPosition: Int,
                        newItemPosition: Int
                    ): Boolean = old[oldItemPosition] == value[newItemPosition]
                })
                    .also { field = value }
                    .dispatchUpdatesTo(this)
            }

        override fun getItem(position: Int): Fragment =
            VideoItemFragment.createInstance(items[position])

        override fun getItemCount() = items.size

        override fun getItemId(position: Int): Long {
            return items[position].id
        }
    }

    companion object {
        const val PREFETCH_OFFSET = 10
    }
}