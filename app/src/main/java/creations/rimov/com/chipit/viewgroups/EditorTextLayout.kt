package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.extensions.gone
import creations.rimov.com.chipit.extensions.visible
import creations.rimov.com.chipit.viewgroups.custom.CustomView
import kotlinx.android.synthetic.main.editor_text.view.*

class EditorTextLayout(context: Context, attrs: AttributeSet)
    : ConstraintLayout(context, attrs),
    CustomView<EditorTextLayout.Handler>,
      View.OnTouchListener {

    object Type {
        const val TITLE = 1
        const val DESC = 2
    }

    private var type: Int = Type.TITLE
    override lateinit var handler: Handler

    private val btnAddText: Button by lazy {btnEditorText}
    private val textLayout: ScrollView by lazy {editorTextLayout}
    private val textView: TextView by lazy {editorText}

    init {
        View.inflate(context, R.layout.editor_text, this)

        context.obtainStyledAttributes(attrs, R.styleable.EditorTextLayout).let {
            btnAddText.text = it.getString(
              R.styleable.EditorTextLayout_btnText)
            btnAddText.setTextColor(it.getColor(
              R.styleable.EditorTextLayout_textColor, 0))
            //Cannot directly set from getDimension, see:
            //  https://stackoverflow.com/questions/26472716/setting-textsize-in-custom-view-results-in-huge-text/26490507#26490507
            btnAddText.setTextSize(
              TypedValue.COMPLEX_UNIT_PX,
              it.getDimensionPixelSize(
                R.styleable.EditorTextLayout_btnTextSize, 0).toFloat())
            textView.setTextSize(
              TypedValue.COMPLEX_UNIT_PX,
              it.getDimensionPixelSize(
                R.styleable.EditorTextLayout_textSize, 0).toFloat())

            it.recycle()
        }

        btnAddText.setOnTouchListener(this)
        textLayout.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        when(v?.id) {
            R.id.btnEditorText -> {
                if(event?.action==MotionEvent.ACTION_UP) {
                    handler.promptEditText(type)

                    return true
                }
            }
            R.id.editorTextLayout -> {
                if(event?.action==MotionEvent.ACTION_UP) {
                    handler.promptEditText(type, textView.text.toString())

                    return true
                }
            }
        }

        return false
    }

    fun displayText(text: String?) {

        if(text==null || text.isBlank()) {
            btnAddText.visible()
            textLayout.gone()
            return
        }

        textView.text = text
        btnAddText.gone()
        textLayout.visible()
    }

    /**
     * @param handler
     * @param opts: opts[0] is Type.TITLE|Type.DESC
     */
    override fun prepare(handler: Handler, vararg opts: Any?) {

        if(opts[0] !is Int?) {
            throw TypeNotPresentException("Bad input to EditorTextLayout::prepare()", null)
        }

        this.handler = handler
        this.type = opts[0] as Int
    }

    /**
     * Means of communicating with parent
     */
    interface Handler {

        fun promptEditText(type: Int, text: String = "")
    }
}