package creations.rimov.com.chipit.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.view_models.GlobalViewModel
import kotlinx.android.synthetic.main.app_layout.*

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    object Constants {
        const val GESTURE_DOWN = 400
        const val GESTURE_UP = 401
        const val GESTURE_LONG_TOUCH = 402
    }

    private var screenHeight: Float = 0f
    private var screenWidth: Float = 0f

    private val globalViewModel: GlobalViewModel by lazy {
        ViewModelProviders.of(this).get(GlobalViewModel::class.java)
    }

    private val navHostFragment: NavHostFragment by lazy {appNavHostFragment as NavHostFragment}

    private val navController: NavController by lazy {navHostFragment.navController}

    private val branchUpButton: ImageButton by lazy {appButtonBranchup}

    private val actionFab: FloatingActionButton by lazy {appFab}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_layout)

        Log.i("Life Cycle", "MainActivity#onCreate(): created!")
        setDisplayDimen()

        navController.addOnDestinationChangedListener(this)

        actionFab.setOnClickListener {
            globalViewModel.touchFab(true)
        }

        branchUpButton.setOnClickListener {
            globalViewModel.touchUp(true)
        }

        globalViewModel.getFabFlag().observe(this, Observer { flag ->

            when {
                flag.display -> {
                    actionFab.show()
                }
                !flag.display -> {
                    actionFab.hide()
                }
                flag.touched -> {
                    actionFab.hide()
                }
            }
        })

        globalViewModel.getUpFlag().observe(this, Observer { flag ->

            when {
                flag.display -> {
                    branchUpButton.visibility = View.VISIBLE
                }
                !flag.display -> {
                    branchUpButton.visibility = View.GONE
                }
                flag.touched -> {
                    actionFab.hide()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        return true
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {

        when(destination.id) {

            R.id.directoryFragment -> {
                Log.i("Navigation", "Destination: Directory")

                actionFab.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_add_fab_image, null))

                globalViewModel.displayFab(true)
            }
            R.id.albumFragment -> {
                Log.i("Navigation", "Destination: Album")

                actionFab.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_add_fab_image, null))

                globalViewModel.displayFab(true)
            }
            R.id.webFragment -> {
                Log.i("Navigation", "Destination: Web")

            }
        }
    }

    override fun onBackPressed() {

        when(navController.currentDestination?.id) {

            R.id.albumFragment -> {
                navController.navigate(R.id.action_albumFragment_to_directoryFragment)

            }
            R.id.webFragment -> {
                navController.navigate(R.id.action_webFragment_to_albumFragment)

            }
        }
    }

    private fun setDisplayDimen() {
        val displayMetrics = resources.displayMetrics

        screenHeight = displayMetrics.heightPixels.toFloat()
        screenWidth = displayMetrics.widthPixels.toFloat()
    }
}