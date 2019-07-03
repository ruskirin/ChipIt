package creations.rimov.com.chipit.activities

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.viewgroups.AppEditorLayout
import creations.rimov.com.chipit.viewgroups.AppToolbarLayout
import kotlinx.android.synthetic.main.app_fab_layout.*
import kotlinx.android.synthetic.main.app_layout.*

class MainActivity
    : AppCompatActivity(), NavController.OnDestinationChangedListener, View.OnClickListener {

    //TODO FUTURE: maybe move screen dimen to globalViewModel?
    private var screenHeight: Float = 0f
    private var screenWidth: Float = 0f

    private val globalViewModel: GlobalViewModel by lazy {
        ViewModelProviders.of(this).get(GlobalViewModel::class.java)
    }

    private val navHostFragment: NavHostFragment by lazy {appNavHostFragment as NavHostFragment}
    private val navController: NavController by lazy {navHostFragment.navController}

    private val toolbar: AppToolbarLayout by lazy {appToolbar}

    private val editor: AppEditorLayout by lazy {appEditor}

    private val fabAction: FloatingActionButton by lazy {appFabAction}
    private val fabCancel: FloatingActionButton by lazy {appFabCancel}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_layout)

        initScreen()

        appTabLayout.setNavController(navController)

        setSupportActionBar(toolbar)

        navController.addOnDestinationChangedListener(this)

        editor.setClickListener(this)
        fabAction.setOnClickListener(this)
        fabCancel.setOnClickListener(this)

        globalViewModel.getChipToEdit().observe(this, Observer { chip ->

            if(chip.isTopic) editor.editTopic(chip)
            else editor.editChip(chip, chip.parentId)
        })

        globalViewModel.getWebParents().observe(this, Observer {
            toolbar.setParents(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater

        when(navController.currentDestination?.id) {

            R.id.directoryFragment -> {
                toolbar.hideSpinner()
            }

            R.id.webFragment -> {
                menuInflater.inflate(R.menu.web_toolbar, menu)
                toolbar.showSpinner()

                return true
            }

            R.id.chipperFragment -> {
                toolbar.hideSpinner()
            }
        }

        return false
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {

        invalidateOptionsMenu()

        when(destination.id) {

            R.id.directoryFragment -> {

            }

            R.id.webFragment -> {
                Log.i("Navigation", "Destination: Web")
            }

            R.id.chipperFragment -> {
                Log.i("Navigation", "Destination: Chipper")
            }
        }
    }

    override fun onClick(view: View?) {

        when(view?.id) {

            //TODO FUTURE: looks like FABs have onVisibilityChangedListeners; could cut down some work
            R.id.appFabAction -> {

                if(editor.isEditing) {
                    val chip = editor.finishEdit(true)

                    chip?.let {
                        //TODO FUTURE: add a snackbar here
                        if(editor.isCreating)
                            globalViewModel.insertChip(it)
                        else
                            globalViewModel.updateChipBasic(it.id, it.name, it.desc, it.imgLocation)
                    }

                    setFabEdit(false)
                    return
                }

                setFabEdit(true)
                editor.isCreating = true //Creating a new Chip

                when(navController.currentDestination?.id) {

                    R.id.directoryFragment -> {
                        editor.editTopic()
                    }

                    R.id.webFragment -> {
                        editor.editChip(parentId = globalViewModel.getObservedChipId())
                    }
                }
            }

            R.id.appFabCancel -> {

                editor.finishEdit(false)
                setFabEdit(false)
            }

            R.id.editorName -> {


            }

            R.id.editorImage -> {
                takePicture()
            }

            R.id.editorDesc -> {


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

    private fun setFabEdit(editing: Boolean) {

        if(editing) {
            fabCancel.show()
            fabAction.setImageResource(R.drawable.ic_check)

        } else {
            fabCancel.hide()
            fabAction.setImageResource(R.drawable.ic_add_fab_image)
        }
    }

    private fun initScreen() {

        val displayMetrics = resources.displayMetrics

        screenHeight = displayMetrics.heightPixels.toFloat()
        screenWidth = displayMetrics.widthPixels.toFloat()

        fabCancel.hide()
    }
}