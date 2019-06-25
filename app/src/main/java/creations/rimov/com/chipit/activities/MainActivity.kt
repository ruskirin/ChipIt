package creations.rimov.com.chipit.activities

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.viewgroups.AppEditorLayout
import kotlinx.android.synthetic.main.app_layout.*

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener, View.OnClickListener {

    //TODO FUTURE: maybe move screen dimen to globalViewModel?
    private var screenHeight: Float = 0f
    private var screenWidth: Float = 0f

    private val globalViewModel: GlobalViewModel by lazy {
        ViewModelProviders.of(this).get(GlobalViewModel::class.java)
    }

    private val navHostFragment: NavHostFragment by lazy {appNavHostFragment as NavHostFragment}
    private val navController: NavController by lazy {navHostFragment.navController}

    private val toolbarConfig: AppBarConfiguration by lazy { AppBarConfiguration(navController.graph)}
    private val toolbar: Toolbar by lazy {appToolbar}
    private val toolbarWebExt: FrameLayout by lazy {toolbarExtWebLayout}

    private val editor: AppEditorLayout by lazy {appEditor}

    private val fab: FloatingActionButton by lazy {appFab}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_layout)

        setDisplayDimen()

        toolbarCollapseLayout.setupWithNavController(toolbar, navController, toolbarConfig)

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

        //TODO FUTURE: can add some init tasks or loading screens from here

        when(destination.id) {

            R.id.directoryFragment -> {
                Log.i("Navigation", "Destination: Directory")

                toolbarWebExt.visibility = View.GONE
            }

            R.id.webFragment -> {
                Log.i("Navigation", "Destination: Album")

                toolbarWebExt.visibility = View.VISIBLE
            }

            R.id.chipperFragment -> {
                Log.i("Navigation", "Destination: Web")

                toolbarWebExt.visibility = View.GONE
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
                        editor.createChip(globalViewModel.getObservedChipId())
                    }
                }
            }

            R.id.editorName -> {


            }

            R.id.editorImage -> {

                takePicture()
            }

            R.id.editorDesc -> {


            }

            R.id.editorBtnSave -> {
                val chip = editor.finishEdit(true)

                chip?.let {
                    //TODO FUTURE: add a snackbar here

                    if(create)
                        globalViewModel.insertChip(it.parentId, it.isTopic, it.name, it.desc, it.counter, it.imgLocation)
                    else
                        globalViewModel.updateChipBasic(it.id, it.name, it.desc, it.imgLocation)
                }

                create = false //Reset flag
            }

            R.id.editorBtnCancel -> {

                editor.finishEdit(false)

                create = false //Reset flag
            }

            R.id.editorBtnDelete -> {

                //TODO FUTURE: decide if you want to keep this
            }
        }
    }

    private fun takePicture() {

        val addChipCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Verifies that an application that can handle this intent exists
        addChipCameraIntent.resolveActivity(this.packageManager)

        val imageFile = CameraUtil.getImageFile(applicationContext) ?: return
        val imageUri = CameraUtil.getImageUri(applicationContext, imageFile.file)

        addChipCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(addChipCameraIntent, CameraUtil.CODE_TAKE_PICTURE)

        if(imageFile.storagePath.isNotEmpty())
            editor.setImage(imageFile.storagePath)
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