package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.Chip
import kotlinx.android.synthetic.main.prompt_confirm.view.*

class PromptConfirmLayout(context: Context, attrs: AttributeSet)
    : ConstraintLayout(context, attrs),
      CustomView<PromptConfirmLayout.Handler>,
      View.OnClickListener {

    private val promptText: TextView by lazy {textPromptConfirm}

    private lateinit var handler: Handler

    init {
        View.inflate(context, R.layout.prompt_confirm, this)

        btnConfirmYes.setOnClickListener(this)
        btnConfirmNo.setOnClickListener(this)
    }

    /**
     * @param handler
     * @param opts[0]: text to display
     */
    override fun prepare(handler: Handler, vararg opts: Any?) {

        this.handler = handler
        (opts[0] as? String)?.let {promptText.text = it}

        Log.i("PromptConfirmLayout", "::prepare(): " +
                                     "layout visible? ${this.isVisible}")
    }

    override fun onClick(v: View?) {

        when(v?.id) {
            R.id.btnConfirmYes -> handler.confirm(true)
            R.id.btnConfirmNo -> handler.confirm(false)
        }
    }

//    fun actionConfirmed(action: Int, chip: Chip?) {
//
//        if(chip==null) return
//
//        when(action) {
//            MainActivity.EditorAction.DELETE -> {
//                val primaryId = globalVM.getFocusId() ?: return
//                //Focused chip is about to be deleted, move up the branch to its
//                // parent
//                if(chip.id==primaryId) {
//                    globalVM.setFocusChip(
//                      toolbar.getParentOfCurrent()?.asChip())
//
//                    toolbar.vanishToolbar(false)
//                }
//
//                localVM.deleteChip(chip)
//            }
//        }
//    }

    interface Handler {

        fun confirm(accept: Boolean)
    }
}