package creations.rimov.com.chipit.activities

import android.os.Bundle
import android.util.Log
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

class DirectoryActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    object Constants {
        const val GESTURE_DOWN = 400
        const val GESTURE_UP = 401
        const val GESTURE_LONG_TOUCH = 402
        const val GESTURE_DOUBLE_TAP = 403
    }

    private val globalViewModel: GlobalViewModel by lazy {
        ViewModelProviders.of(this).get(GlobalViewModel::class.java)
    }

    private val navHostFragment: NavHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.directory_nav_host) as NavHostFragment
    }
    private val navController: NavController by lazy {
        navHostFragment.navController
    }

    private val branchUpButton: ImageButton by lazy {
        findViewById<ImageButton>(R.id.directory_layout_button_branchup)
    }

    private val actionFab: FloatingActionButton by lazy {
        findViewById<FloatingActionButton>(R.id.directory_layout_fab_action)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.directory_layout)

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

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {

        when(destination.id) {

            R.id.directoryFragment -> {
                Log.i("Navigation", "Destination: Directory")

                actionFab.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_add_fab_image, null))

                globalViewModel.displayFab(true)
            }
            R.id.webFragment -> {
                Log.i("Navigation", "Destination: Web")

                actionFab.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_add_fab_image, null))

                globalViewModel.displayFab(true)
            }
            R.id.chipFragment -> {
                Log.i("Navigation", "Destination: Chip")

                actionFab.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.mipmap.ic_edit, null))

                globalViewModel.displayFab(false)
            }
        }
    }

    //
    override fun onBackPressed() {

        when(navController.currentDestination?.id) {

            R.id.webFragment -> {
                globalViewModel.displayFab(true)

                navController.navigate(R.id.action_webFragment_to_directoryFragment)

            }
            R.id.chipFragment -> {
                globalViewModel.displayFab(true)

                navController.navigate(R.id.action_chipFragment_to_webFragment)

            }
        }
    }
}