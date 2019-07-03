package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.NavController
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.extensions.gone
import creations.rimov.com.chipit.extensions.visible
import kotlinx.android.synthetic.main.app_tab_layout.view.*

class AppTabLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs), View.OnClickListener {

    private lateinit var navController: NavController

    private val web: TextView by lazy {tabWeb}
    private val chips: ImageButton by lazy {tabChips}
    private val storage: ImageButton by lazy {tabStorage}

    init {
        View.inflate(context, R.layout.app_tab_layout, this)

        tabTopics.setOnClickListener(this)
        web.setOnClickListener(this)
        chips.setOnClickListener(this)
        storage.setOnClickListener(this)
    }

    override fun onClick(view: View?) {

        when(view?.id) {

            R.id.tabTopics -> {
                showWeb()

                if(navController.currentDestination?.id == R.id.directoryFragment)
                    return

                navController.navigate(R.id.action_webFragment_to_directoryFragment)
            }

            R.id.tabWeb -> {
                hideWeb()
            }

            R.id.tabChips -> {
                if(navController.currentDestination?.id == R.id.directoryFragment) {
                    navController.navigate(R.id.action_directoryFragment_to_webFragment)
                }
            }

            R.id.tabStorage -> {
                if(navController.currentDestination?.id == R.id.directoryFragment) {
                    navController.navigate(R.id.action_directoryFragment_to_webFragment)
                }
            }
        }
    }

    fun setNavController(controller: NavController) {
        navController = controller
    }

    private fun hideWeb() {

        web.gone()
        chips.visible()
        storage.visible()
    }

    private fun showWeb() {

        web.visible()
        chips.gone()
        storage.gone()
    }
}