package creations.rimov.com.chipit.activities

import android.animation.AnimatorInflater
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipReference
import creations.rimov.com.chipit.fragments.ChipperFragmentDirections
import creations.rimov.com.chipit.objects.ChipUpdateBasic
import creations.rimov.com.chipit.objects.CoordPoint
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.view_models.EditorViewModel
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.viewgroups.AppEditorLayout
import creations.rimov.com.chipit.viewgroups.AppToolbarLayout
import kotlinx.android.synthetic.main.app_fab_layout.*
import kotlinx.android.synthetic.main.app_layout.*

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    View.OnClickListener, AppEditorLayout.EditorHandler, AppToolbarLayout.ToolbarHandler {

    object EditorAction {
        const val CREATE = 300
        const val EDIT = 301
        const val UPDATE = 302
        const val DELETE = 303
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

    private val fabAction: FloatingActionButton by lazy {appFabAction}
    private val fabCancel: FloatingActionButton by lazy {appFabCancel}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_layout)

        initScreen()

        editor.setHandler(this)
        toolbar.setHandler(this)

        navController.addOnDestinationChangedListener(this)

        editor.setClickListener(this)
        fabAction.setOnClickListener(this)
        fabCancel.setOnClickListener(this)

        globalViewModel.getWebTransition().observe(this, Observer { forward ->

            if(forward) {
                animToolbarVanish(false)
                fabAction.hide()

            } else {
                animToolbarVanish(true)
                fabAction.show()
            }
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
                    if(chip.id == globalViewModel.getFocusId().value)
                        globalViewModel.setFocusId(toolbar.getParentOfCurrent()?.id)

                    animToolbarVanish(true)

                    editViewModel.deleteChip(chip)
                }
            }
        })

        globalViewModel.getWebParents().observe(this, Observer {
            toolbar.setParents(it)
        })
    }

    override fun onClick(view: View?) {

        when(view?.id) {

            R.id.appFabAction -> {
                //Saving startEdit
                if(editViewModel.isEditing && editor.finishEdit(true)) {
                    editViewModel.saveEdit()

                    editor.clearData()
                    setFabEdit(false)
                    return
                }

                if(navController.currentDestination?.id == R.id.directoryFragment) {
                    startChipCreate(true,null, null)
                    return
                }

                startChipCreate(false, globalViewModel.getFocusId().value, null)
            }

            R.id.appFabCancel -> {

                editor.finishEdit(false)
                editViewModel.isEditing = false
                setFabEdit(false)
            }

            R.id.editorBtnImageCamera -> {
                takePicture()
            }

            R.id.editorBtnImageStorage -> {
                selectPicture()
            }

            R.id.editorBtnImageUrl -> {

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

                return true
            }
        }

        return false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId) {

            android.R.id.home -> {
                if(navController.currentDestination?.id == R.id.chipperFragment) {
                    val directions =
                        ChipperFragmentDirections.actionChipperFragmentToWebFragment(globalViewModel.getFocusId().value ?: -1L)
                    navController.navigate(directions)

                    return true
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    //After startActivityForResult()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != Activity.RESULT_OK) return

        when(requestCode) {

            CameraUtil.CODE_GET_IMAGE -> {
                editor.showImage(data?.data)
            }

            CameraUtil.CODE_TAKE_PICTURE -> {
                editor.showImage(data?.extras?.get("data"))
            }
        }
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {

        invalidateOptionsMenu()

        when(destination.id) {

            R.id.directoryFragment -> {
                animToolbarVanish(true)
            }

            R.id.webFragment -> {
                animToolbarVanish(true)
            }

            R.id.chipperFragment -> {

            }
        }
    }

    override fun setSelectedChip(chip: ChipReference) {
        Log.i("Touch Event", "Main#setSelectedChip(): setting id ${chip.id}, name ${chip.name}")

        globalViewModel.setFocusId(chip.id)
    }

    override fun updateName(text: String) {
        editViewModel.setName(text)
    }

    override fun updateDesc(text: String) {
        editViewModel.setDesc(text)
    }

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

        val imageFile = CameraUtil.getImageFileOld() ?: return

        addChipCameraIntent.putExtra(
              MediaStore.EXTRA_OUTPUT, CameraUtil.getImageUri(applicationContext, imageFile.file))
        startActivityForResult(addChipCameraIntent, CameraUtil.CODE_TAKE_PICTURE)

        if(imageFile.storagePath.isNotEmpty())
            editViewModel.editingChip?.imgLocation = imageFile.storagePath //Save
    }

    private fun selectPicture() {

        val getChipPhotoIntent = Intent(Intent.ACTION_GET_CONTENT)
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

    private var isToolbarVanish = false //Keep track of the status of the toolbar
    private fun animToolbarVanish(reverse: Boolean) {

        if(isToolbarVanish == !reverse) return //Toolbar already in the desired state

        val animator = AnimatorInflater.loadAnimator(
            this, if(reverse) R.animator.toolbar_vanish_reverse else R.animator.toolbar_vanish)

        animator.setTarget(toolbar)
        animator.start()

        isToolbarVanish = !reverse //Set the flag
    }
}