package creations.rimov.com.chipit.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.view_models.GlobalViewModel

class DirectoryActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private val globalViewModel: GlobalViewModel by lazy {
        ViewModelProviders.of(this).get(GlobalViewModel::class.java)
    }

    private val navHostFragment: NavHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.directory_nav_host) as NavHostFragment
    }
    private val navController: NavController by lazy {
        navHostFragment.navController
    }

    private val addChipFab: FloatingActionButton by lazy {
        findViewById<FloatingActionButton>(R.id.directory_layout_fab_action)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.directory_layout)

        navController.addOnDestinationChangedListener(this)

        addChipFab.setOnClickListener { view ->

            if(addChipFab.isOrWillBeShown)
                globalViewModel.setFabTouched(true)

            addChipFab.hide()
        }
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {

        when(destination.id) {

            R.id.directoryFragment -> {
                Log.i("Navigation", "Destination: Directory")

                globalViewModel.setFabTouched(false)

                addChipFab.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_add_fab_image, null))

                addChipFab.show()
            }
            R.id.webFragment -> {
                Log.i("Navigation", "Destination: Web")

                globalViewModel.setFabTouched(false)

                addChipFab.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_add_fab_image, null))

                addChipFab.show()
            }
            R.id.chipFragment -> {
                Log.i("Navigation", "Destination: Chip")

                globalViewModel.setFabTouched(false)

                addChipFab.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.mipmap.ic_edit, null))

                addChipFab.show()
            }
        }
    }

    //
    override fun onBackPressed() {

        when(navController.currentDestination?.id) {

            R.id.webFragment -> {
                globalViewModel.setFabTouched(false)

                navController.navigate(R.id.action_webFragment_to_directoryFragment)

            }
            R.id.chipFragment -> {
                globalViewModel.setFabTouched(false)

                navController.navigate(R.id.action_chipFragment_to_webFragment)

            }
        }
    }
}