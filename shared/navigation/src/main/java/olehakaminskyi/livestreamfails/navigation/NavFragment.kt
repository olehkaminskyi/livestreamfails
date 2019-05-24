package olehakaminskyi.livestreamfails.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class NavFragment: NavHostFragment () {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = activity
        if (activity is NavHostContainer) {
            activity.navigationController = navController
        }
    }
}

interface NavHostContainer {
    var navigationController: NavController
}