package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import creations.rimov.com.chipit.R
import kotlinx.android.synthetic.main.editor_chip_layout.view.*

class ChipEditorLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    init {
        View.inflate(context, R.layout.editor_chip_layout, this)
    }

    fun setTouchListener(listener: OnTouchListener) {

        chipEditorBtnEdit.setOnTouchListener(listener)
        chipEditorBtnDelete.setOnTouchListener(listener)
    }

    fun show(show: Boolean) {

        if(show) this.visibility = View.VISIBLE
        else this.visibility = View.GONE
    }
}