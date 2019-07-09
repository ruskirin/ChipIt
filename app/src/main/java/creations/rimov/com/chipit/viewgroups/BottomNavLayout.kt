package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import creations.rimov.com.chipit.R
import kotlinx.android.synthetic.main.web_detail_bottom_nav_layout.view.*

class BottomNavLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    init {
        View.inflate(context, R.layout.web_detail_bottom_nav_layout, this)
    }

    fun setTouchListener(listener: OnTouchListener) {

        webDetailBtnDesc.setOnTouchListener(listener)
        webDetailBtnChip.setOnTouchListener(listener)
        webDetailBtnSettings.setOnTouchListener(listener)
        webDetailBtnDelete.setOnTouchListener(listener)
    }
}