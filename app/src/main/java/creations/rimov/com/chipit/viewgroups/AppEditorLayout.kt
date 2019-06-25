package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import kotlinx.android.synthetic.main.app_editor_layout.view.*

class AppEditorLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private lateinit var chipEdit: Chip

    private val image: ImageView by lazy {editorImage}
    private val name: TextInputEditText by lazy {editorName}
    private val desc: TextInputEditText by lazy {editorDesc}
    private val btnDelete: ImageButton by lazy {editorBtnDelete}

    init {
        View.inflate(context, R.layout.app_editor_layout, this)
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

        initChip(null, true)

        startTopicEdit(true)
    }

    fun createChip(parentId: Long?) {

        initChip(parentId, false)

        startChipEdit(true)
    }

    /**Display the necessary windows, and set the initial EditText values**/
    fun editTopic(chip: Chip) {

        chipEdit = chip

        setNameAndDesc(chip.name, chip.desc)

        startTopicEdit(false)
    }

    fun editChip(chip: Chip) {

        chipEdit = chip

        setNameAndDesc(chip.name, chip.desc)

        startChipEdit(false)
    }

    fun finishEdit(save: Boolean): Chip? {

        if(!save || name.text.isNullOrBlank()) {
            this.visibility = View.GONE
            return null
        }

        chipEdit.name = name.text.toString()
        chipEdit.desc = desc.text.toString()

        this.visibility = View.GONE
        return chipEdit
    }

    fun setImage(imgPath: String) {

        chipEdit.imgLocation = imgPath

        Glide.with(image.context)
            .load(imgPath)
            .into(image)
    }

    fun setNameTextWatcher(textWatcher: TextWatcher) {
        name.addTextChangedListener(textWatcher)
    }

    fun setDescTextWatcher(textWatcher: TextWatcher) {
        desc.addTextChangedListener(textWatcher)
    }

    private fun startTopicEdit(create: Boolean) {

        this.visibility = View.VISIBLE
        image.visibility = View.GONE

        if(!create) btnDelete.visibility = View.VISIBLE
        else btnDelete.visibility = View.GONE
    }

    private fun startChipEdit(create: Boolean) {

        this.visibility = View.VISIBLE
        image.visibility = View.VISIBLE

        if(!create) btnDelete.visibility = View.VISIBLE
        else btnDelete.visibility = View.GONE
    }

    private fun setNameAndDesc(name: String, desc: String) {

        if(name.isNotEmpty())
            this.name.setText(name)
        if(desc.isNotEmpty())
            this.desc.setText(desc)
    }

    private fun initChip(parentId: Long?, isTopic: Boolean) {
        chipEdit = Chip(0L, parentId, isTopic = isTopic)

        name.setText("")
        desc.setText("")
        Glide.with(image.context)
            .load(resources.getDrawable(R.drawable.ic_photo_empty, null))
            .into(image)
    }
}