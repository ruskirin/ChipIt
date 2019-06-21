package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.Chip
import kotlinx.android.synthetic.main.editor_layout.view.*

class EditorLayout(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private lateinit var chipEdit: Chip

    private val imageFrame: FrameLayout by lazy {editorImageFrame}
    private val image: ImageView by lazy {editorImage}
    private val name: TextInputEditText by lazy {editorName}
    private val desc: TextInputEditText by lazy {editorDesc}
    private val btnDelete: ImageButton by lazy {editorBtnDelete}

    init {
        View.inflate(context, R.layout.editor_layout, this)
    }

    fun setClickListener(listener: OnClickListener) {

        image.setOnClickListener(listener)
        name.setOnClickListener(listener)
        desc.setOnClickListener(listener)
        editorBtnSave.setOnClickListener(listener)
        editorBtnCancel.setOnClickListener(listener)
        btnDelete.setOnClickListener(listener)
    }

    fun createTopic() {

        startTopicEdit()
    }

    fun createChip(parentId: Long?) {

        startChipEdit()
    }

    /**Display the necessary windows, and set the initial EditText values**/
    fun editTopic(chip: Chip) {

        chipEdit = chip

        setNameAndDesc(chip.name, chip.desc)

        startTopicEdit()
    }

    fun editChip(chip: Chip) {

        chipEdit = chip

        setNameAndDesc(chip.name, chip.desc)

        startChipEdit()
    }

    fun finishEdit(save: Boolean): Chip? {

        if(save) {
            if(!::chipEdit.isInitialized || !chipEdit.isTopic)
                TODO("save image")

            chipEdit.name = name.text.toString()
            chipEdit.desc = desc.text.toString()

            return chipEdit
        }

        this.visibility = View.GONE
        return null
    }

    fun setNameTextWatcher(textWatcher: TextWatcher) {
        name.addTextChangedListener(textWatcher)
    }

    fun setDescTextWatcher(textWatcher: TextWatcher) {
        desc.addTextChangedListener(textWatcher)
    }

    private fun startTopicEdit() {

        this.visibility = View.VISIBLE
        imageFrame.visibility = View.GONE
        btnDelete.visibility = View.VISIBLE
    }

    private fun startChipEdit() {

        this.visibility = View.VISIBLE
        imageFrame.visibility = View.VISIBLE
        btnDelete.visibility = View.VISIBLE
    }

    private fun setNameAndDesc(name: String, desc: String) {

        if(name.isNotEmpty())
            this.name.setText(name)
        if(desc.isNotEmpty())
            this.desc.setText(desc)
    }
}