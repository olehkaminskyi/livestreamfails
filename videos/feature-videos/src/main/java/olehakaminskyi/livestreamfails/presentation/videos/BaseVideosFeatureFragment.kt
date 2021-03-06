package olehakaminskyi.livestreamfails.presentation.videos

import olehakaminskyi.livestreamfails.domaininjection.videos.KoinDomainVideosModules
import olehakaminskyi.livestreamfails.player.injection.KainPlayerModules
import olehakaminskyi.livestreamfails.presentation.BaseFragment
import olehakaminskyi.livestreamfails.presentation.videos.item.VideoItemViewModel
import olehakaminskyi.livestreamfails.presentation.videos.posts.VideoPostsViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

open class BaseVideosFeatureFragment : BaseFragment() {
    companion object {
        init {
            loadKoinModules(
                *KoinDomainVideosModules,
                *KainPlayerModules,
                module {
                    viewModel { VideoPostsViewModel(get()) }
                    viewModel { (id: Long) -> VideoItemViewModel(get(), get(), id) }
                }
            )
        }
    }
}