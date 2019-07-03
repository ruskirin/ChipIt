package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.extensions.gone
import creations.rimov.com.chipit.extensions.visible
import kotlinx.android.synthetic.main.app_editor_layout.view.*
import kotlinx.android.synthetic.main.app_editor_prompt_add_image.view.*

class AppEditorLayout(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    var isEditing = this.isVisible

    private lateinit var chipEdit: Chip

    private val image: ImageView by lazy {editorImage}
    private val nameLayout: TextInputLayout by lazy {editorNameLayout}
    private val name: TextInputEditText by lazy {editorName}
    private val descLayout: TextInputLayout by lazy {editorDescLayout}
    private val desc: TextInputEditText by lazy {editorDesc}
    private val btnName: Button by lazy {editorBtnName}
    private val btnDesc: Button by lazy {editorBtnDesc}
    private val btnImageLayout by lazy {editorBtnImageLayout}
    private val btnImageCamera: ImageButton by lazy {editorBtnImageCamera}
    private val btnImageStorage: ImageButton by lazy {editorBtnImageStorage}
    private val btnImageUrl: ImageButton by lazy {editorBtnImageUrl}

    init {
        View.inflate(context, R.layout.app_editor_layout, this)
    }

    fun setClickListener(listener: OnClickListener) {

    }

    //Set up the view depending on whether editing or creating
    fun setEdit(create: Boolean) {

        showImage(!create)
        showName(!create)
        showDesc(!create)
    }

    private fun showImage(show: Boolean) {

        if(show) {
            btnImageLayout.gone()
            image.visible()

        } else {
            btnImageLayout.visible()
            image.gone()
        }
    }

    private fun showName(show: Boolean) {

        if(show) {
            btnName.gone()
            nameLayout.visible()

        } else {
            btnName.visible()
            nameLayout.gone()
        }
    }

    private fun showDesc(show: Boolean) {

        if(show) {
            btnDesc.gone()
            descLayout.visible()

        } else {
            btnDesc.visible()
            descLayout.gone()
        }
    }
}