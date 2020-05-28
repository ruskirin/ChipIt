package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.constants.EditorConsts
import creations.rimov.com.chipit.extensions.displayImage
import creations.rimov.com.chipit.extensions.gone
import creations.rimov.com.chipit.extensions.visible
import kotlinx.android.synthetic.main.editor_material.view.*
import kotlinx.android.synthetic.main.editor_material_image.view.*

class EditorMatPrevLayout(context: Context, attrs: AttributeSet)
    : ConstraintLayout(context, attrs),
      CustomView<EditorMatPrevLayout.Handler>,
      View.OnClickListener {

    override lateinit var handler: Handler

    private val btnAddMat: Button by lazy {btnAddMatPrev}
    private val img: ImageView by lazy {promptPreviewImg}
    private val videoLayout by lazy {matPreviewVideoLayout}
    private val audioLayout by lazy {matPreviewAudioLayout}

    init {
        View.inflate(context, R.layout.editor_material, this)

        btnAddMat.setOnClickListener(this)
        img.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(::handler.isInitialized) handler.promptAddMat()
    }

    override fun prepare(handler: Handler, vararg opts: Any?) {
        this.handler = handler
    }

    /**
     * For when a resource already exists and needs to be displayed
     */
    fun <T> display(matType: Int?, matPath: T?) {

        when(matType) {
            EditorConsts.IMAGE -> {
                btnAddMat.gone()
                img.visible()
                videoLayout.gone()
                audioLayout.gone()

                img.displayImage(matPath)
            }
            EditorConsts.VIDEO  -> {
                btnAddMat.gone()
                img.gone()
                videoLayout.visible()
                audioLayout.gone()
            }
            EditorConsts.AUDIO  -> {
                btnAddMat.gone()
                img.gone()
                videoLayout.gone()
                audioLayout.visible()
            }
            EditorConsts.TEXT  -> {
                //TODO HIGH: implement text material
            }
            else -> {
                btnAddMat.visible()
                img.gone()
                videoLayout.gone()
                audioLayout.gone()

                //TODO HIGH: check that all materials are discarded
                img.displayImage(null)
            }
        }
    }


    interface Handler {

        fun promptAddMat()
    }
}