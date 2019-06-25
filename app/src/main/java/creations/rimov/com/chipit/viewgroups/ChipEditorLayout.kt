package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import creations.rimov.com.chipit.R
import kotlinx.android.synthetic.main.editor_chip_layout.view.*

class ChipEditorLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val btnEdit: ImageButton by lazy {chipEditorBtnEdit}
    private val btnDelete: ImageButton by lazy {chipEditorBtnDelete}

    init {
        View.inflate(context, R.layout.editor_chip_layout, this)
    }

    fun setTouchListener(listener: OnTouchListener) {

        btnEdit.setOnTouchListener(listener)
        btnDelete.setOnTouchListener(listener)
    }

    fun show(deleteOnly: Boolean) {

        if(deleteOnly) btnEdit.visibility = View.GONE
        else btnEdit.visibility = View.VISIBLE

        this.visibility = View.VISIBLE
    }

    fun hide() {
        this.visibility = View.GONE
    }
}