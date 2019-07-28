package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.objects.ChipUpdateBasic
import creations.rimov.com.chipit.extensions.gone
import creations.rimov.com.chipit.extensions.visible
import kotlinx.android.synthetic.main.app_editor_layout.view.*

class AppEditorLayout(context: Context, attrs: AttributeSet)
    : ConstraintLayout(context, attrs), View.OnClickListener, TextWatcher, View.OnFocusChangeListener {

    private object Text {
        const val NAME = 101
        const val DESC = 102
    }

    private lateinit var handler: EditorHandler

    private val btnName: Button by lazy {editorBtnName}
    private val nameLayout: TextInputLayout by lazy {editorNameLayout}
    private val name: TextInputEditText by lazy {editorName}

    private val btnDesc: Button by lazy {editorBtnDesc}
    private val descLayout: TextInputLayout by lazy {editorDescLayout}
    private val desc: TextInputEditText by lazy {editorDesc}

    private val image: ImageView by lazy {editorImage}
    private val imageText: TextView by lazy {editorBtnImage}

    private var editText: Int = 0

    init {
        View.inflate(context, R.layout.app_editor_layout, this)

        btnName.setOnClickListener(this)
        btnDesc.setOnClickListener(this)
        editorBtnImage.setOnClickListener(this)

        image.setOnClickListener(this)

        name.onFocusChangeListener = this
        desc.onFocusChangeListener = this

        name.addTextChangedListener(this)
        desc.addTextChangedListener(this)
    }

    /**Click listener for UI triggers**/
    override fun onClick(view: View?) {

        when(view?.id) {

            R.id.editorBtnName -> {
                showName(true)
            }

            R.id.editorBtnImage -> {
                handler.promptImage()
            }

            R.id.editorBtnDesc -> {
                showDesc(true)
            }

            R.id.editorImage -> {
                handler.promptImage()
            }
        }
    }

    fun setHandler(handler: EditorHandler) {
        this.handler = handler
    }

    fun startEdit(chip: ChipUpdateBasic? = null) {

        chip?.let{
            displayChip(it)
            return
        }

        //Creating new Chip, Prepare UI
        this.visible()
        showName(false)
        showDesc(false)
        showImage(null) //No image to show
    }

    //Editing completed; return the new Chip information or null if canceled
    fun finishEdit(save: Boolean): Boolean {

        if(!save) {
            this.gone()
            clearData()
            return true
        }

        if(name.text.isNullOrBlank()) {
            Log.i("Touch Event", "AppEditorLayout#finishEdit(): chip was not given a name!")

            Toast.makeText(this.context, R.string.editor_message_noname, Toast.LENGTH_SHORT).show()
            return false
        }

        this.gone()
        return true
    }

    private fun displayChip(chip: ChipUpdateBasic) {

        this.visible()

        if(chip.name.isNotBlank()) showName(true, chip.name)
        else showName(false)

        if(chip.desc.isNotBlank()) showDesc(true, chip.desc)
        else showDesc(false)

        showImage(chip.imgLocation)
    }

    //To indicate whether name or desc is being edited
    override fun onFocusChange(v: View?, hasFocus: Boolean) {

        when(v?.id) {

            R.id.editorName -> {
                if(hasFocus) editText = Text.NAME
            }

            R.id.editorDesc -> {
                if(hasFocus) editText = Text.DESC
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

        s?.let {
            when(editText) {
                Text.NAME -> {
                    handler.updateName(it.toString())
                }

                Text.DESC -> {
                    handler.updateDesc(it.toString())
                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    fun <T> showImage(imgLocation: T?) {

        imgLocation?.let {

            Glide.with(image.context)
                .load(it)
                .apply(RequestOptions()
                           .override(image.width, image.height))
                .into(image)

            imageText.gone()
            image.visible()

            return
        }

        imageText.visible()
        image.gone()
    }

    private fun showName(show: Boolean, name: String = "") {

        if(show) {
            if(name.isNotBlank()) this.name.setText(name)

            btnName.gone()
            nameLayout.visible()

        } else {
            btnName.visible()
            nameLayout.gone()
        }
    }

    private fun showDesc(show: Boolean, desc: String = "") {

        if(show) {
            if(desc.isNotBlank()) this.desc.setText(desc)

            btnDesc.gone()
            descLayout.visible()

        } else {
            btnDesc.visible()
            descLayout.gone()
        }
    }

    fun clearData() {

        name.setText("")
        desc.setText("")
        image.setImageDrawable(null)
    }

    interface EditorHandler {

        fun updateName(text: String)

        fun updateDesc(text: String)

        fun promptImage()
    }
}