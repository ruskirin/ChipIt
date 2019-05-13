package creations.rimov.com.chipit.activities

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.Topic
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.view_models.DirectoryViewModel
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.view_models.WebViewModel
import java.io.IOException

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

        addChipFab.setOnClickListener {

            globalViewModel.setFabTouched(true)

            addChipFab.hide()
        }
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {

        when(destination.id) {

            R.id.directoryFragment -> {
                globalViewModel.setFabTouched(false)

                addChipFab.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_add_fab_image, null))

                addChipFab.show()
            }
            R.id.webFragment -> {
                globalViewModel.setFabTouched(false)

                addChipFab.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_add_fab_image, null))

                addChipFab.show()
            }
            R.id.chipFragment -> {
                globalViewModel.setFabTouched(false)

                addChipFab.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.mipmap.ic_edit, null))

                addChipFab.show()
            }
        }
    }
}