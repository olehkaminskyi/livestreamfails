package olehakaminskyi.livestreamfails.presentation.onboarding.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _finishSplash = MutableLiveData<Boolean>()
    val finishSplashViewModel: LiveData<Boolean> get() = _finishSplash

    init {
        viewModelScope.launch {
            delay(SPLASH_DELAY)
            _finishSplash.value = true
        }
    }

    companion object {
        const val SPLASH_DELAY = 3_000L
    }
}