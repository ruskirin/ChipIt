package creations.rimov.com.chipit.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.constants.EditorConsts
import creations.rimov.com.chipit.database.objects.ChipReference
import creations.rimov.com.chipit.extensions.getViewModel
import creations.rimov.com.chipit.extensions.nav
import creations.rimov.com.chipit.fragments.DirectoryFragmentDirections
import creations.rimov.com.chipit.fragments.WebFragmentDirections
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.viewgroups.MainToolbarLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
    NavController.OnDestinationChangedListener,
    View.OnClickListener,
    MainToolbarLayout.ToolbarHandler {

    object Constant {
        const val REQUEST_WRITE_EXTERNAL_STORAGE = 1000
    }

    //TODO FUTURE: maybe move screen dimen to globalViewModel?
    private var screenH: Float = 0f
    private var screenW: Float = 0f

    private val globalVM: GlobalViewModel by lazy {
        getViewModel<GlobalViewModel>()
    }

    private val navHostFragment: NavHostFragment by lazy {
        mainNavHost as NavHostFragment
    }
    private val navController: NavController by lazy {
        navHostFragment.navController
    }

    private val toolbar: MainToolbarLayout by lazy {mainToolbar}

    private val fab: FloatingActionButton by lazy {mainFab}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initScreen()

        toolbar.setHandler(this)

        navController.addOnDestinationChangedListener(this)

        fab.setOnClickListener(this)

        globalVM.getWebTransition().observe(this, Observer { forward ->
            //TODO FUTURE: see if this is necessary
        })

        globalVM.getWebParents().observe(this, Observer {
            toolbar.setParents(it)
        })
    }

    override fun onClick(view: View?) {

        when(view?.id) {
            R.id.mainFab       -> {
                if(navController.currentDestination?.id==R.id.editorFragment) {
                    globalVM.setEditAction(EditorConsts.SAVE)
                    return
                }

                navigateTo(R.id.editorFragment, EditorConsts.CREATE)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater

        when(navController.currentDestination?.id) {
            R.id.editorFragment -> {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                toolbar.hideSpinner()

                return true
            }
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
                    R.id.editorFragment -> {
                        globalVM.setAction(EditorConsts.CANCEL)
                        navController.navigateUp()
                    }
                    R.id.webFragment -> {
                        navController.navigateUp()
                        return true
                    }
                    R.id.chipperFragment -> {
                        navController.navigateUp()
                        return true
                    }
                }
            }
            R.id.toolbarChipperEdit -> {
                navigateTo(R.id.editorFragment, EditorConsts.EDIT)
            }
            R.id.toolbarChipperDelete -> {
                navigateTo(R.id.editorFragment, EditorConsts.DELETE)
            }
        }

        return false
    }

    override fun onDestinationChanged(
          controller: NavController,
          destination: NavDestination,
          arguments: Bundle?) {

        invalidateOptionsMenu()

        Log.i("MainActivity", "::onDestinationChanged(): " +
                              "current ${destination.label}")

        when(destination.id) {
            R.id.editorFragment -> {
                setFabEdit(true)
                toolbar.vanishToolbar(false)
            }
            R.id.directoryFragment -> {
                globalVM.setFocusChip(null, false) //Reset the focus chip

                setFabEdit(false)
                toolbar.vanishToolbar(false)
            }
            R.id.webFragment -> {
                setFabEdit(false)
                toolbar.vanishToolbar(false)
            }
            R.id.chipperFragment   -> {
                setFabEdit(false)
                fab.hide()
                toolbar.vanishToolbar(true)
            }
        }
    }

    override fun setSelectedChip(chip: ChipReference) {
        Log.i("MainActivity", "::setSelectedChip(): new primary " +
                             "chip ${chip.id}")

        globalVM.setFocusChip(chip.asChip(), false)
    }

    private fun navigateTo(fragId: Int, args: Any?) {

        when(fragId) {
            R.id.editorFragment -> {
                if(args !is Int) {
                    Toast.makeText(this,
                                   "Cannot navigate to fragment -- wrong arg passed",
                                   Toast.LENGTH_SHORT).show()
                    return
                }

                globalVM.setEditAction(args)

                when(navController.currentDestination?.id) {
                    R.id.directoryFragment -> {
                        navController.nav(
                          DirectoryFragmentDirections
                              .actionDirectoryFragmentToEditorFragment(args))
                    }
                    R.id.webFragment -> {
                        navController.nav(
                          WebFragmentDirections
                              .actionWebFragmentToEditorFragment(args))
                    }
                    R.id.chipperFragment -> {
                        //TODO: implement if needed
                    }
                }
            }
            R.id.webFragment -> {
                if(args !is Long) {
                    Toast.makeText(
                      this,
                      "Cannot navigate to fragment -- wrong arg passed",
                      Toast.LENGTH_SHORT).show()

                    return
                }
                navController.nav(
                  DirectoryFragmentDirections.actionDirectoryFragmentToWebFragment(args))
            }
            R.id.chipperFragment -> {
                if(args !is Long) {
                    Toast.makeText(
                      this,
                      "Cannot navigate to fragment -- wrong arg passed",
                      Toast.LENGTH_SHORT).show()

                    return
                }

                navController.nav(
                  WebFragmentDirections.actionWebFragmentToChipperFragment(args))
            }
        }
    }

    private fun setFabEdit(editing: Boolean) {
        fab.show()

        if(editing) {
            fab.setImageResource(R.drawable.ic_check)
        }
        else {
            fab.setImageResource(R.drawable.ic_add_fab_image)
        }
    }

    private fun initScreen() {

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val displayMetrics = resources.displayMetrics

        screenH = displayMetrics.heightPixels.toFloat()
        screenW = displayMetrics.widthPixels.toFloat()
    }
}