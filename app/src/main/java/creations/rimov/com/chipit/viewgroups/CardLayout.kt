package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import creations.rimov.com.chipit.R

class CardLayout(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    init {
        View.inflate(context, R.layout.card_layout, this)
    }
}