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
import kotlinx.android.synthetic.main.fragment_videos.*

open class VideoPostsFragment : BaseVideosFeatureFragment() {

    private val viewModel: VideoPostsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            olehakaminskyi.livestreamfails.presentation.videos.R.layout.fragment_videos,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoPostsViewPager.adapter = PagerAdapter(this).also { adapter ->
            viewModel.posts.observe(viewLifecycleOwner, Observer {
                it.error { ::handleError }
                it.success { adapter.items = it }
            })
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onResume() {
        super.onResume()
        viewModel.resume()
    }

    private fun handleError(errorResult: ResultError) {
    }

    class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        var items: List<UiVideoPost> = emptyList()
            set(value) {
                calculateDiff(object : DiffUtil.Callback() {
                    override fun areItemsTheSame(
                        oldItemPosition: Int,
                        newItemPosition: Int
                    ): Boolean = field[oldItemPosition].id == value[newItemPosition].id

                    override fun getOldListSize(): Int = field.size

                    override fun getNewListSize(): Int = value.size

                    override fun areContentsTheSame(
                        oldItemPosition: Int,
                        newItemPosition: Int
                    ): Boolean = field[oldItemPosition] == value[newItemPosition]
                })
                    .also { field = value }
                    .dispatchUpdatesTo(this)
            }

        override fun getItem(position: Int): Fragment =
            VideoItemFragment.createInstance(items[position])

        override fun getItemCount() = items.size
    }
}