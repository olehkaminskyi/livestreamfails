package olehakaminskyi.livestreamfails

import android.os.Bundle
import androidx.navigation.NavController
import olehakaminskyi.livestreamfails.navigation.NavHostContainer
import olehakaminskyi.livestreamfails.presentation.BaseActivity

class MainActivity : BaseActivity(), NavHostContainer {
    override lateinit var navigationController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
