package creations.rimov.com.chipit.activities

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
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
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.extensions.gone
import creations.rimov.com.chipit.extensions.visible
import creations.rimov.com.chipit.objects.ChipUpdateBasic
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.view_models.EditorViewModel
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.viewgroups.AppEditorLayout
import creations.rimov.com.chipit.viewgroups.AppToolbarLayout
import kotlinx.android.synthetic.main.app_fab_layout.*
import kotlinx.android.synthetic.main.app_layout.*

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    View.OnClickListener, AppEditorLayout.EditorHandler {

    object EditorAction {
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
    private val tabLayout: TabLayout by lazy {appTabLayout}

    private val editor: AppEditorLayout by lazy {appEditor}

    private val fabAction: FloatingActionButton by lazy {appFabAction}
    private val fabCancel: FloatingActionButton by lazy {appFabCancel}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_layout)

        initScreen()

        editor.setHandler(this)

        setSupportActionBar(toolbar)

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {

                when(tab?.position) {
                    0 -> {
                        if(navController.currentDestination?.id == R.id.webFragment)
                            navController.navigate(R.id.action_webFragment_to_directoryFragment)
                    }

                    1 -> {
                        if(navController.currentDestination?.id == R.id.directoryFragment)
                            navController.navigate(R.id.action_directoryFragment_to_webFragment)
                    }
                }
            }
        })

        navController.addOnDestinationChangedListener(this)

        editor.setClickListener(this)
        fabAction.setOnClickListener(this)
        fabCancel.setOnClickListener(this)

        globalViewModel.getWebTransition().observe(this, Observer { progress ->

            if(progress > 0.9f) fabAction.hide()
            else fabAction.show()
        })

        globalViewModel.getChipAction().observe(this, Observer { chipAction ->

            //TODO NULL-CHECK (proper)
            val chip = chipAction.getChip() ?: return@Observer

            when(chipAction.getAction()) {

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

                //Creating new Chip
                setFabEdit(true)
                editor.startEdit()

                if(navController.currentDestination?.id == R.id.directoryFragment) {
                    editViewModel.startCreate(true, null)
                    return
                }

                editViewModel.startCreate(false, globalViewModel.observedChipId)
                return
            }

            R.id.appFabCancel -> {

                editor.finishEdit(false)
                editViewModel.isEditing = false
                setFabEdit(false)
            }

            R.id.editorName -> {


            }

            R.id.editorDesc -> {


            }

            R.id.editorBtnImageCamera -> {
                takePicture()
            }

            R.id.editorBtnImageStorage -> {

            }

            R.id.editorBtnImageUrl -> {

            }
        }
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
                setSelectedTab(0)
            }

            R.id.webFragment -> {
                setSelectedTab(1)
            }

            R.id.chipperFragment -> {

            }
        }
    }

    override fun updateName(text: String) {
        editViewModel.setName(text)
    }

    override fun updateDesc(text: String) {
        editViewModel.setDesc(text)
    }

    private fun takePicture() {

        val addChipCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Verifies that an application that can handle this intent exists
        addChipCameraIntent.resolveActivity(this.packageManager)

        val imageFile = CameraUtil.getImageFile() ?: return
        val imageUri = CameraUtil.getImageUri(applicationContext, imageFile.file)

        addChipCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(addChipCameraIntent, CameraUtil.CODE_TAKE_PICTURE)

        if(imageFile.storagePath.isNotEmpty()) {

            editViewModel.editingChip?.imgLocation = imageFile.storagePath //Save

            editor.showImage(imageFile.storagePath)
        }
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

    //Set the selected tab to the position
    private fun setSelectedTab(position: Int) {

        if(tabLayout.selectedTabPosition != position)
            tabLayout.selectTab(tabLayout.getTabAt(position))
    }

    private fun initScreen() {

        if(editViewModel.isEditing) {
            editor.startEdit(editViewModel.editingChip)
            setFabEdit(true)
        }

        val displayMetrics = resources.displayMetrics

        screenHeight = displayMetrics.heightPixels.toFloat()
        screenWidth = displayMetrics.widthPixels.toFloat()

        fabCancel.hide()
    }
}