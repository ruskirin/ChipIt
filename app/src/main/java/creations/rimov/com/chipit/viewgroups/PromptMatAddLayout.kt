package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.constants.EditorConsts
import creations.rimov.com.chipit.extensions.gone
import creations.rimov.com.chipit.extensions.visible
import kotlinx.android.synthetic.main.prompt_add_material.view.*

class PromptMatAddLayout(context: Context, attrs: AttributeSet)
    : FrameLayout(context, attrs),
      View.OnClickListener,
      CustomView<PromptMatAddLayout.Handler> {

    private val layoutPromptAddFrom by lazy {layoutBtnAddFrom}

    private val btnNew by lazy {btnPromptAddNew}
    private val btnStorage by lazy {btnPromptAddStorage}

    private lateinit var handler: Handler

    private var type: Int = 0

    //TODO (HIGH): see if better to move code from init() to some lifecycle trigger
    init {
        View.inflate(context, R.layout.prompt_add_material, this)

        layoutBtnAddMat.setOnClickListener(this)
        layoutPromptAddFrom.setOnClickListener(this)

        btnPromptAddImage.setOnClickListener(this)
        btnPromptAddVideo.setOnClickListener(this)
        btnPromptAddAudio.setOnClickListener(this)
        btnPromptAddText.setOnClickListener(this)

        btnNew.setOnClickListener(this)
        btnStorage.setOnClickListener(this)
    }

    override fun prepare(handler: Handler, vararg opts: Any?) {
        this.handler = handler
    }

    override fun onClick(v: View?) {

        when(v?.id) {
            R.id.layoutBtnAddFrom -> {
                layoutPromptAddFrom.gone()
            }
            R.id.btnPromptAddImage   -> {
                type = EditorConsts.IMAGE
                promptFrom()
            }
            R.id.btnPromptAddVideo   -> {
                type = EditorConsts.VIDEO
                promptFrom()
            }
            R.id.btnPromptAddAudio   -> {
                type = EditorConsts.AUDIO
                promptFrom()
            }
            R.id.btnPromptAddText    -> {
                type = EditorConsts.TEXT
                promptFrom()
            }
            R.id.btnPromptAddNew     -> {
                handler.getMaterial(
                  when(type) {
                      EditorConsts.IMAGE -> EditorConsts.NEW_IMAGE
                      EditorConsts.VIDEO -> EditorConsts.NEW_VIDEO
                      EditorConsts.AUDIO -> EditorConsts.NEW_AUDIO
                      EditorConsts.TEXT  -> EditorConsts.NEW_TEXT
                      else -> throw TypeNotPresentException(
                        "Invalid input!", null)
                  }
                )
            }
            R.id.btnPromptAddStorage -> {
                handler.getMaterial(
                  when(type) {
                      EditorConsts.IMAGE -> EditorConsts.STOR_IMAGE
                      EditorConsts.VIDEO -> EditorConsts.STOR_VIDEO
                      EditorConsts.AUDIO -> EditorConsts.STOR_AUDIO
                      EditorConsts.TEXT  -> EditorConsts.STOR_TEXT
                      else -> throw TypeNotPresentException(
                        "Invalid input!", null)
                  }
                )
            }
        }
    }

    private fun promptFrom() {

        layoutPromptAddFrom.visible()

        when(type) {
            EditorConsts.IMAGE -> {
                btnNew.text = resources.getString(R.string.prompt_btn_camera)
                btnNew.setCompoundDrawablesWithIntrinsicBounds(
                  resources.getDrawable(R.drawable.ic_camera, context.theme),
                  null, null, null)
            }
            EditorConsts.VIDEO -> {
                btnNew.text = resources.getString(R.string.prompt_btn_record)
                btnNew.setCompoundDrawablesWithIntrinsicBounds(
                  resources.getDrawable(R.drawable.ic_addvideo, context.theme),
                  null, null, null)
            }
            EditorConsts.AUDIO -> {
                btnNew.text = resources.getString(R.string.prompt_btn_record)
                btnNew.setCompoundDrawablesWithIntrinsicBounds(
                  resources.getDrawable(R.drawable.ic_addaudio, context.theme),
                  null, null, null)
            }
            EditorConsts.TEXT  -> {
                btnNew.text = resources.getString(R.string.prompt_btn_type)
                btnNew.setCompoundDrawablesWithIntrinsicBounds(
                  resources.getDrawable(R.drawable.ic_keyboard, context.theme),
                  null, null, null)
            }
        }
    }


    interface Handler {

        fun getMaterial(from: Int)
    }
}