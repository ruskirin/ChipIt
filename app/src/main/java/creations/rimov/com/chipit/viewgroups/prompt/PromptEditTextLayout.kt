package creations.rimov.com.chipit.viewgroups.prompt

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.constants.EditorConsts
import creations.rimov.com.chipit.viewgroups.custom.CustomView
import kotlinx.android.synthetic.main.prompt_text.view.*

class PromptEditTextLayout(context: Context, attrs: AttributeSet)
    : ConstraintLayout(context, attrs),
    CustomView<PromptEditTextLayout.Handler>,
      View.OnClickListener {

    /**
     * Values taken from:
     *   https://developer.android.com/reference/android/widget/TextView.html#attr_android:inputType
     */
    private object InputTypes {
        const val TEXT = 1
        const val TEXTMULTI = 20001
    }

    override lateinit var handler: Handler

    private var type: Int = 0 //EditorConsts.EDIT_TITLE | EditorConsts.EDIT_DESC

    private val editTextParent: TextInputLayout by lazy {promptTextParent}
    private val editText: TextInputEditText by lazy {promptText}

    private val btnSave: Button by lazy {btnPromptTextSave}
    private val btnCancel: Button by lazy {btnPromptTextCancel}

    init {
        View.inflate(context, R.layout.prompt_text, this)

        btnSave.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
    }

    /**
     * @param handler
     * @param opts[0]: EditorConsts.EDIT_TITLE | EditorConsts.EDIT_DESC
     *            [1]: String for EditText to display
     */
    override fun prepare(handler: Handler, vararg opts: Any?) {
        this.handler = handler

        opts[0]?.let {
            if(it !is Int) return

            this.type = it

            when(it) {
                EditorConsts.EDIT_TITLE -> {
                    editTextParent.counterMaxLength = resources.getInteger(
                      R.integer.editor_title_maxlength)
                    editText.inputType =
                        InputTypes.TEXT
                    editText.hint = resources.getString(
                      R.string.editor_title_def)
                }
                EditorConsts.EDIT_DESC -> {
                    editTextParent.counterMaxLength = resources.getInteger(
                      R.integer.editor_desc_maxlength)
                    editText.inputType =
                        InputTypes.TEXTMULTI
                    editText.hint = resources.getString(
                      R.string.editor_desc_def)
                }
            }

            editText.setText(opts[1] as CharSequence)
        }
    }

    override fun onClick(v: View?) {

        when(v?.id) {
            R.id.btnPromptTextSave -> {
                handler.finishEdit(type, editText.text.toString())
            }
            R.id.btnPromptTextCancel -> {
                handler.finishEdit()
            }
        }
    }


    interface Handler {

        fun finishEdit(type: Int = 0, text: String = "")
    }
}