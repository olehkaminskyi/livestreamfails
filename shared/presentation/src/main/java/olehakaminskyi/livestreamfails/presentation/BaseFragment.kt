package olehakaminskyi.livestreamfails.presentation

import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    val baseActivity: BaseActivity by lazy {
        activity as BaseActivity
    }
}