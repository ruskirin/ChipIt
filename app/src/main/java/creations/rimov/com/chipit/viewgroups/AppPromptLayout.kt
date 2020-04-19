package creations.rimov.com.chipit.viewgroups

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.activities.MainActivity
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.extensions.gone
import creations.rimov.com.chipit.extensions.visible
import kotlinx.android.synthetic.main.prompt_confirm.view.*
import kotlinx.android.synthetic.main.main_prompt.view.*
import kotlinx.android.synthetic.main.prompt_addmaterial_livestorage.view.*

class AppPromptLayout(context: Context, attrs: AttributeSet)
    : ConstraintLayout(context, attrs), View.OnClickListener {

    object Prompt {
        const val CAMERA: Int = 5000
        const val STORAGE: Int = 5001
    }

    private val addImageLayout: View by lazy {promptAddLiveStorage}

    private val confirmPromptText: TextView by lazy {promptConfirmText}
    private val btnYes: Button by lazy {promptConfirmYes}
    private val btnNo: Button by lazy {promptConfirmNo}

    private lateinit var handler: PromptHandler

    var chip: Chip? = null
    var action: Int = 0

    init {
        View.inflate(context, R.layout.main_prompt, this)

        this.setOnClickListener(this)
        btnYes.setOnClickListener(this)
        btnNo.setOnClickListener(this)
        promptBtnLive.setOnClickListener(this)
        promptBtnStorage.setOnClickListener(this)
    }

    override fun onClick(view: View?) {

        if(view == this) toggleVis(false)

        when(view?.id) {

            R.id.promptConfirmYes -> {
                //TODO FUTURE: add user notification

                handler.actionConfirmed(action, chip)
                toggleVis(false)
            }

            R.id.promptConfirmNo  -> {

                handler.actionDenied(action, chip)
                toggleVis(false)
            }

            R.id.promptBtnLive    -> {
                handler.getImageFrom(Prompt.CAMERA)
                toggleVis(false)
            }

            R.id.promptBtnStorage -> {
                handler.getImageFrom(Prompt.STORAGE)
                toggleVis(false)
            }
        }
    }

    fun confirm(action: Int, chip: Chip) {

        this.action = action
        this.chip = chip

        showConfirm()
        toggleVis(true)

        when(action) {

            MainActivity.EditorAction.DELETE -> {
                confirmPromptText.setText(resources.getString(R.string.prompt_text_delete, chip.name))
            }
        }
    }

    fun setHandler(handler: PromptHandler) {
        this.handler = handler
    }

    fun clear() {
        toggleVis(false)
    }

    fun showAddImage() {

        btnYes.gone()
        btnNo.gone()
        confirmPromptText.gone()

        toggleVis(true)

        addImageLayout.visible()
    }

    private fun showConfirm() {

        addImageLayout.gone()

        btnYes.visible()
        btnNo.visible()
        confirmPromptText.visible()
    }

    private fun toggleVis(show: Boolean) {

        if(show)
            ObjectAnimator.ofFloat(this, "alpha", 0f, 1f).apply {
                duration = 500
                addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationStart(animation: Animator?) {
                        this@AppPromptLayout.visible()
                    }
                })

                start()
            }

        else
            ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
                duration = 500
                addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator?) {
                        this@AppPromptLayout.gone()
                    }
                })

                start()
            }
    }

    interface PromptHandler {

        fun actionConfirmed(action: Int, chip: Chip?)

        fun actionDenied(action: Int, chip: Chip?)

        fun getImageFrom(choice: Int)
    }
}