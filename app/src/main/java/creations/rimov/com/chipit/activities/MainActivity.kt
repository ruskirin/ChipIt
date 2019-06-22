package creations.rimov.com.chipit.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.viewgroups.AppEditorLayout
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

    private val editor: AppEditorLayout by lazy {appEditor}

    private val fab: FloatingActionButton by lazy {appFab}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_layout)

        Log.i("Life Cycle", "MainActivity#onCreate(): created!")
        setDisplayDimen()

        navController.addOnDestinationChangedListener(this)

        editor.setClickListener(this)
        fab.setOnClickListener(this)

        globalViewModel.getChipToEdit().observe(this, Observer { chip ->

            if(chip.isTopic) editor.editTopic(chip)
            else editor.editChip(chip)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        return true
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {

        when(destination.id) {

            R.id.directoryFragment -> {
                Log.i("Navigation", "Destination: Directory")
            }

            R.id.webFragment -> {
                Log.i("Navigation", "Destination: Album")
            }

            R.id.chipperFragment -> {
                Log.i("Navigation", "Destination: Web")

            }
        }
    }

    override fun onBackPressed() {

        when(navController.currentDestination?.id) {
            R.id.webFragment -> {
                navController.navigate(R.id.action_webFragment_to_directoryFragment)

            }

            R.id.chipperFragment -> {
                navController.navigate(R.id.action_chipperFragment_to_webFragment)

            }
        }
    }

    private var create: Boolean = false //Flag to indicate the action performed in Editor

    override fun onClick(view: View?) {

        when(view?.id) {
            //TODO FUTURE: looks like FABs have onVisibilityChangedListeners; could cut down some work
            R.id.appFab -> {
                create = true //Creating a new Chip

                when(navController.currentDestination?.id) {

                    R.id.directoryFragment -> {
                        editor.createTopic()
                    }

                    R.id.webFragment -> {
//                        editor.createChip(parentid)
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

                val chip = editor.finishEdit(true)

                chip?.let {
                    Snackbar.make(fab, "Chip saved", Snackbar.LENGTH_SHORT)
                    Log.i("Touch Event", "MainActivity#onClick(): chip saved! Topic? ${it.isTopic}")

                    if(create) globalViewModel.insertChip(it)
                    else globalViewModel.updateChipBasic(chip.id, chip.name, chip.desc, chip.imgLocation)
                }

                create = false
            }

            R.id.editorBtnCancel -> {

                editor.finishEdit(false)

                create = false
            }

            R.id.editorBtnDelete -> {

                //TODO FUTURE: decide if you want to keep this
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