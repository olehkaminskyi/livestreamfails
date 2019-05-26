package olehakaminskyi.livestreamfails.presentation.onboarding.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import olehakaminskyi.livestreamfails.presentation.onboarding.BaseOnBoardingFeatureFragment
import olehakaminskyi.livestreamfails.presentation.onboarding.R
import org.koin.android.viewmodel.ext.android.viewModel

class SplashFragment : BaseOnBoardingFeatureFragment() {
    private val _viewModel: SplashViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _viewModel.finishSplashViewModel.observe(viewLifecycleOwner,
            Observer { if (it) navigateToVideoPosts() })
    }

    private fun navigateToVideoPosts() {
        findNavController().navigate(R.id.onboarding_to_videos)
    }
}