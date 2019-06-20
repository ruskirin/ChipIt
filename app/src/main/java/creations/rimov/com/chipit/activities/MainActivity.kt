package creations.rimov.com.chipit.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.viewgroups.EditorLayout
import kotlinx.android.synthetic.main.app_layout.*

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener, View.OnClickListener {

    object Constants {
        const val GESTURE_DOWN = 400
        const val GESTURE_UP = 401
        const val GESTURE_LONG_TOUCH = 402
    }

    //TODO FUTURE: maybe move screen dimen to globalViewModel?
    private var screenHeight: Float = 0f
    private var screenWidth: Float = 0f

    private val globalViewModel: GlobalViewModel by lazy {
        ViewModelProviders.of(this).get(GlobalViewModel::class.java)
    }

    private val navHostFragment: NavHostFragment by lazy {appNavHostFragment as NavHostFragment}
    private val navController: NavController by lazy {navHostFragment.navController}

    private val editor: EditorLayout by lazy {appEditor}

    private val fab: FloatingActionButton by lazy {appFab}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_layout)

        Log.i("Life Cycle", "MainActivity#onCreate(): created!")
        setDisplayDimen()

        navController.addOnDestinationChangedListener(this)

        editor.setClickListener(this)
        fab.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        return true
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {

        when(destination.id) {

            R.id.directoryFragment -> {
                Log.i("Navigation", "Destination: Directory")

                fab.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_add_fab_image, null))
            }

            R.id.albumFragment -> {
                Log.i("Navigation", "Destination: Album")

                fab.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_add_fab_image, null))
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

    override fun onClick(view: View?) {

        when(view?.id) {
            //TODO FUTURE: looks like FABs have onVisibilityChangedListeners; could cut down some work
            R.id.appFab -> {
                when(navController.currentDestination?.id) {

                    R.id.directoryFragment -> {
                        editor.startTopicEdit()
                    }

                    R.id.albumFragment -> {
                        editor.startChipEdit()
                    }
                }
            }

            R.id.editorName -> {


            }

            R.id.editorImage -> {


            }

            R.id.editorDesc -> {


            }

            R.id.editorBtnSave -> {


            }

            R.id.editorBtnCancel -> {


            }

            R.id.editorBtnDelete -> {


            }
        }
    }

    private fun toggleFab() {

        if(fab.isOrWillBeShown) fab.hide()
        else fab.show()
    }

    private fun setDisplayDimen() {
        val displayMetrics = resources.displayMetrics

        screenHeight = displayMetrics.heightPixels.toFloat()
        screenWidth = displayMetrics.widthPixels.toFloat()
    }
}