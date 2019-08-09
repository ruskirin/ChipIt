package creations.rimov.com.chipit.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipReference
import creations.rimov.com.chipit.fragments.ChipperFragmentDirections
import creations.rimov.com.chipit.fragments.WebFragmentDirections
import creations.rimov.com.chipit.objects.ChipAction
import creations.rimov.com.chipit.objects.ChipUpdateBasic
import creations.rimov.com.chipit.objects.CoordPoint
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.view_models.EditorViewModel
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.viewgroups.AppEditorLayout
import creations.rimov.com.chipit.viewgroups.AppToolbarLayout
import creations.rimov.com.chipit.viewgroups.AppPromptLayout
import kotlinx.android.synthetic.main.app_fab_layout.*
import kotlinx.android.synthetic.main.app_layout.*

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    View.OnClickListener, AppEditorLayout.EditorHandler, AppPromptLayout.PromptHandler, AppToolbarLayout.ToolbarHandler {

    object EditorAction {
        const val CREATE = 300
        const val EDIT = 301
        const val UPDATE = 302
        const val DELETE = 303
    }

    object Constant {
        const val REQUEST_WRITE_EXTERNAL_STORAGE = 1000
    }

    //TODO FUTURE: maybe move screen dimen to globalViewModel?
    private var screenHeight: Float = 0f
    private var screenWidth: Float = 0f

    private val globalViewModel: GlobalViewModel by lazy {
        ViewModelProviders.of(this).get(GlobalViewModel::class.java)
    }
    private val editViewModel: EditorViewModel by lazy {
        ViewModelProviders.of(this).get(EditorViewModel::class.java)
    }

    private val navHostFragment: NavHostFragment by lazy {appNavHostFragment as NavHostFragment}
    private val navController: NavController by lazy {navHostFragment.navController}

    private val toolbar: AppToolbarLayout by lazy {appToolbar}
    private val editor: AppEditorLayout by lazy {appEditor}
    private val prompt: AppPromptLayout by lazy {appPrompt}

    private val fabAction: FloatingActionButton by lazy {appFabAction}
    private val fabCancel: FloatingActionButton by lazy {appFabCancel}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_layout)

        initScreen()

        editor.setHandler(this)
        toolbar.setHandler(this)
        prompt.setHandler(this)

        navController.addOnDestinationChangedListener(this)

        fabAction.setOnClickListener(this)
        fabCancel.setOnClickListener(this)

        globalViewModel.getWebTransition().observe(this, Observer { forward ->
            //TODO FUTURE: see if this is necessary
        })

        globalViewModel.getChipAction().observe(this, Observer { chipAction ->

            //TODO NULL-CHECK (proper)
            val chip = chipAction.getChip() ?: return@Observer

            when(chipAction.getAction()) {

                EditorAction.CREATE -> {
                    startChipCreate(chip.isTopic, chip.parentId, chip.vertices)
                }

                EditorAction.EDIT -> {
                    setFabEdit(true)
                    editViewModel.startEdit(chip)
                    editor.startEdit(editViewModel.editingChip)
                }

                EditorAction.UPDATE -> {
                    editViewModel.updateChipBasic(
                        ChipUpdateBasic.instance(chip.id, chip.parentId, chip.isTopic, chip.name, chip.desc, chip.imgLocation))
                }

                EditorAction.DELETE -> {
                    prompt.confirm(EditorAction.DELETE, chip)
                }
            }
        })

        globalViewModel.getWebParents().observe(this, Observer {
            toolbar.setParents(it)
        })
    }

    override fun onClick(view: View?) {

        when(view?.id) {

            R.id.appFabAction     -> {
                //Saving startEdit
                if(editViewModel.isEditing && editor.finishEdit(true)) {
                    editViewModel.saveEdit()

                    editor.clearData()
                    setFabEdit(false)
                    return
                }
                //Creating a Topic Chip
                if(navController.currentDestination?.id == R.id.directoryFragment) {
                    startChipCreate(true,null, null)
                    return
                }

                //Creating a regular Chip
                startChipCreate(false, globalViewModel.getPrimaryChip().value?.id, null)
            }

            R.id.appFabCancel     -> {

                editor.finishEdit(false)
                prompt.clear()
                editViewModel.isEditing = false
                setFabEdit(false)
            }

            R.id.editorImage      -> {
                //TODO FUTURE: need to be able to repick your image...
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater

        when(navController.currentDestination?.id) {

            R.id.directoryFragment -> {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)

                toolbar.hideSpinner()

                return true
            }

            R.id.webFragment -> {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)

                menuInflater.inflate(R.menu.web_toolbar, menu)
                toolbar.showSpinner()

                return true
            }

            R.id.chipperFragment -> {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                
                toolbar.hideSpinner()
                menuInflater.inflate(R.menu.chipper_toolbar, menu)

                return true
            }
        }

        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {

            android.R.id.home -> {
                when(navController.currentDestination?.id) {
                    R.id.webFragment -> {
                        val directions =
                            WebFragmentDirections.actionWebFragmentToDirectoryFragment()
                        navController.navigate(directions)

                        return true
                    }

                    R.id.chipperFragment -> {
                        val directions = ChipperFragmentDirections
                            .actionChipperFragmentToWebFragment(globalViewModel.getPrimaryId() ?: -1L)
                        navController.navigate(directions)

                        return true
                    }
                }
            }

            R.id.toolbarChipperEdit -> {
                globalViewModel.setChipAction(
                      ChipAction.instance(globalViewModel.getPrimaryChip().value ?: return false, EditorAction.EDIT))
            }

            R.id.toolbarChipperDelete -> {
                globalViewModel.setChipAction(
                      ChipAction.instance(globalViewModel.getPrimaryChip().value ?: return false, EditorAction.DELETE))
            }
        }

        return false
    }

    //After startActivityForResult()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != Activity.RESULT_OK) return

        when(requestCode) {

            CameraUtil.CODE_GET_IMAGE -> {
                data?.let {
                    editor.showImage(it.data)

                    editViewModel.editingChip?.imgLocation = it.data?.toString() ?: "" //Save
                }
            }

            CameraUtil.CODE_TAKE_PICTURE -> {
                data?.let {
                    editor.showImage(it.extras?.get("data"))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode) {

            Constant.REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    takePicture()

                //TODO FUTURE: storage write permission has not been granted, inform the user
            }
        }
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {

        invalidateOptionsMenu()

        when(destination.id) {

            R.id.directoryFragment -> {
                fabAction.show()
                toolbar.vanishToolbar(false)
            }

            R.id.webFragment -> {
                fabAction.show()
                toolbar.vanishToolbar(false)
            }

            R.id.chipperFragment -> {
                fabAction.hide()
                toolbar.vanishToolbar(true)
            }
        }
    }

    override fun setSelectedChip(chip: ChipReference) {
        Log.i("Touch Event", "Main#setSelectedChip(): new primary chip ${chip.id}")

        globalViewModel.setPrimaryChip(chip.asChip())
    }

    override fun updateName(text: String) {
        editViewModel.setName(text)
    }

    override fun updateDesc(text: String) {
        editViewModel.setDesc(text)
    }

    override fun promptImage() {
        prompt.showAddImage()
    }

    //AppPromptLayout Handler-------------------------------------------------------------
    override fun actionConfirmed(action: Int, chip: Chip?) {

        if(chip == null) return

        when(action) {

            EditorAction.DELETE -> {
                val primaryId = globalViewModel.getPrimaryChip().value?.id ?: return
                //Focused chip is about to be deleted, move up the branch to its parent
                if(chip.id == primaryId) {
                    globalViewModel.setPrimaryChip(toolbar.getParentOfCurrent()?.asChip())

                    toolbar.vanishToolbar(false)
                }

                editViewModel.deleteChip(chip)
            }
        }
    }

    override fun actionDenied(action: Int, chip: Chip?) {}

    override fun getImageFrom(choice: Int) {

        when(choice) {
            AppPromptLayout.Prompt.CAMERA -> {
                //Check if storage write permission has already been granted; else request it
                if(getStorageWritePermission())
                    takePicture()
            }

            AppPromptLayout.Prompt.STORAGE -> {
                selectPicture()
            }

            AppPromptLayout.Prompt.URL -> {

            }
        }
    }
    //---------------------------------------------------------------------------------

    private fun startChipCreate(isTopic: Boolean, parentId: Long?, vertices: MutableList<CoordPoint>?) {

        setFabEdit(true)
        editor.startEdit()

        editViewModel.startCreate(isTopic, parentId, vertices)
    }

    private fun takePicture() {

        val addChipCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Verifies that an application that can handle this intent exists
        if(addChipCameraIntent.resolveActivity(packageManager) == null) {
            Log.e("Touch Event", "Main#takePicture(): no app to handle ACTION_IMAGE_CAPTURE!")
            return
        }

        var uri: Uri? = null

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            val imageFile = CameraUtil.getImageFile() ?: return

            uri = CameraUtil.getImageUri(applicationContext, imageFile)

        } else {
            uri = CameraUtil.getImageUriNew(applicationContext)
        }

        addChipCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(addChipCameraIntent, CameraUtil.CODE_TAKE_PICTURE)

        editViewModel.editingChip?.imgLocation = uri.toString() //Save
    }

    private fun selectPicture() {

        val getChipPhotoIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            .apply {type = "image/*"}
        getChipPhotoIntent.resolveActivity(packageManager)
            ?.let {startActivityForResult(getChipPhotoIntent, CameraUtil.CODE_GET_IMAGE)}
    }

    private fun setFabEdit(editing: Boolean) {

        if(editing) {
            fabAction.show()
            fabCancel.show()
            fabAction.setImageResource(R.drawable.ic_check)

        } else {
            fabCancel.hide()
            fabAction.setImageResource(R.drawable.ic_add_fab_image)
        }
    }

    private fun initScreen() {

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if(editViewModel.isEditing) {
            editor.startEdit(editViewModel.editingChip)
            setFabEdit(true)
        }

        val displayMetrics = resources.displayMetrics

        screenHeight = displayMetrics.heightPixels.toFloat()
        screenWidth = displayMetrics.widthPixels.toFloat()

        fabCancel.hide()
    }

    //Since SDK 23(24?), permission must be requested at runtime if it has not already been granted
    private fun getStorageWritePermission(): Boolean {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
              == PackageManager.PERMISSION_GRANTED) {
            return true //Permission has already been granted
        }

        //Explain why you need the permission
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //TODO FUTURE: display rationale for this request
            Toast.makeText(this, "Need permission please!", Toast.LENGTH_SHORT).show()
        }

        //Permission has not yet been granted, check onRequestPermissionResult()
        ActivityCompat.requestPermissions(
              this,
              arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
              Constant.REQUEST_WRITE_EXTERNAL_STORAGE)

        return false
    }
}