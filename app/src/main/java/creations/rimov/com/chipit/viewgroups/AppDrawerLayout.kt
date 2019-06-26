package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import creations.rimov.com.chipit.R
import kotlinx.android.synthetic.main.drawer_layout.view.*

class AppDrawerLayout(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val btnTopics: ImageButton by lazy {drawerTopics}
    private val btnChips: ImageButton by lazy {drawerChips}
    private val btnChipper: ImageButton by lazy {drawerChipper}

    init {
        View.inflate(context, R.layout.drawer_layout, this)
    }

    fun setClickListener(listener: OnClickListener) {

        btnTopics.setOnClickListener(listener)
        btnChips.setOnClickListener(listener)
        btnChipper.setOnClickListener(listener)
    }
}