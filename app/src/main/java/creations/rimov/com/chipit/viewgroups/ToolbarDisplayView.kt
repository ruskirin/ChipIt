package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipIdentity
import kotlinx.android.synthetic.main.toolbar_layout.view.*

class ToolbarDisplayView(context: Context,
                         attrs: AttributeSet) : AppBarLayout(context, attrs) {

    private val image: ImageView by lazy {toolbarExtImg}
    private val desc: TextView by lazy {toolbarExtDesc}
    private val layout: FrameLayout by lazy {toolbarExtLayout}
    private val toolbar: Toolbar by lazy {appToolbar}

    init {
        View.inflate(context, R.layout.toolbar_layout, this)
    }

    fun getAppToolbar() = toolbar

    fun setDisplay(chip: ChipIdentity,
                   screenHeight: Float, screenWidth: Float,
                   percentHeight: Float = 1f, percentWidth: Float = 1f) {


        setDimen(screenHeight, screenWidth, percentHeight, percentWidth)
        setContent(chip)
    }

    private fun setContent(chip: ChipIdentity) {

        if(layout.isVisible) {

            desc.text = chip.desc
            Glide.with(this).load(chip.imgLocation).into(image)
        }
    }

    //Set image dimen as percentage of screen dimen
    fun setDimen(screenHeight: Float, screenWidth: Float, percentHeight: Float, percentWidth: Float) {
        val height = (screenHeight * percentHeight).toInt()
        val width = (screenWidth * percentWidth).toInt()

        layout.layoutParams = CollapsingToolbarLayout.LayoutParams(width, height)
    }

    fun hideExtContent() {
        layout.visibility = View.GONE
    }

    fun showExtContent() {
        layout.visibility = View.VISIBLE
    }
}