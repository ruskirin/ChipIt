package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.transition.AutoTransition
import androidx.transition.ChangeBounds
import androidx.transition.Scene
import com.bumptech.glide.Glide
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.extensions.gone
import creations.rimov.com.chipit.extensions.visible
import kotlinx.android.synthetic.main.editor_detail_layout.view.*
import kotlinx.android.synthetic.main.web_detail_layout.view.*

class WebDetailLayout(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val image: ImageView by lazy {webDetailImg}
    private val desc: TextView by lazy {webDetailDesc}
//    private val btnEdit: ImageButton by lazy {webDetailBtnSettings}


    init {
        View.inflate(context, R.layout.web_detail_layout, this)
    }

    fun setChip(chip: ChipIdentity?) {

        if(chip == null || chip.isTopic) {
            this.visibility = View.GONE
            return
        }

        this.visibility = View.VISIBLE

        desc.text = chip.desc
        Glide.with(image.context).load(chip.imgLocation).into(image)
    }

    //TODO FUTURE: see if clearing listeners is necessary
    fun setTouchListener(listener: OnTouchListener) {

//        btnEdit.setOnTouchListener(listener)
//        webDetailBtnDesc.setOnTouchListener(listener)
        detailEditorBtnEdit.setOnTouchListener(listener)
        detailEditorBtnDelete.setOnTouchListener(listener)
    }

    fun toggleDesc() {

        if(desc.isVisible) desc.visibility = View.GONE
        else desc.visibility = View.VISIBLE
    }
}