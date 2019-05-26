package olehakaminskyi.livestreamfails.presentation.onboarding

import olehakaminskyi.livestreamfails.presentation.BaseFragment
import olehakaminskyi.livestreamfails.presentation.onboarding.splash.SplashViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

open class BaseOnBoardingFeatureFragment : BaseFragment() {
    companion object {
        init {
            loadKoinModules(module {
                viewModel { SplashViewModel() }
            })
        }
    }
}