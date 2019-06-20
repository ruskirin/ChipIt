package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import creations.rimov.com.chipit.R
import kotlinx.android.synthetic.main.editor_layout.view.*

class EditorLayout(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val image: ImageView by lazy {editorImage}
    private val name: EditText by lazy {editorName}
    private val desc: EditText by lazy {editorDesc}
    private val btnDelete: ImageButton by lazy {editorBtnDelete}

    private val DEF_NAME: String
    private val DEF_DESC: String
    private val DEF_IMAGE: Drawable

    init {
        View.inflate(context, R.layout.editor_layout, this)

        DEF_NAME = resources.getString(R.string.editor_name_def)
        DEF_DESC = resources.getString(R.string.editor_desc_def)
        DEF_IMAGE = resources.getDrawable(R.drawable.ic_photo_empty, null)
    }

    fun setClickListener(listener: OnClickListener) {

        image.setOnClickListener(listener)
        name.setOnClickListener(listener)
        desc.setOnClickListener(listener)
        editorBtnSave.setOnClickListener(listener)
        editorBtnCancel.setOnClickListener(listener)
        btnDelete.setOnClickListener(listener)
    }

    /**Display the necessary windows, and set the initial EditText values**/
    fun startTopicEdit(name: String = DEF_NAME,
                       desc: String = DEF_DESC) {

        this.visibility = View.VISIBLE

        setEditTextDef(name, desc)

        showImage(false)
        showDelete(false)
    }

    fun startChipEdit(name: String = DEF_NAME,
                      desc: String = DEF_DESC,
                      imageLoc: String = "") {

        TODO()
    }

    private fun saveEdit() {

        this.visibility = View.GONE
    }

    private fun showImage(show: Boolean) {

        image.visibility =
            if(show) View.VISIBLE
            else View.GONE
    }

    private fun showDelete(show: Boolean) {

        btnDelete.visibility =
            if(show) View.VISIBLE
            else View.GONE
    }

    private fun setEditTextDef(name: String, desc: String) {

        this.name.setText(name)
        this.desc.setText(desc)
    }
}