package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipIdentity
import kotlinx.android.synthetic.main.web_detail_layout.view.*

class WebDetailLayout(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val image: ImageView by lazy {webDetailImg}
    private val desc: TextView by lazy {webDetailDesc}

    init {
        View.inflate(context, R.layout.web_detail_layout, this)
    }

    fun setChip(chip: ChipIdentity) {

        if(chip.isTopic) {
            this.visibility = View.GONE
            return
        }

        this.visibility = View.VISIBLE

        desc.text = chip.desc
        Glide.with(image.context)
            .load(chip.imgLocation)
            .into(image)
    }

    fun setTouchListener(listener: OnTouchListener) {
        val btnDesc = webDetailBtnDesc

        btnDesc.setOnTouchListener(null)

        btnDesc.setOnTouchListener(listener)
    }

    fun toggleDesc() {

        if(desc.isVisible) desc.visibility = View.GONE
        else desc.visibility = View.VISIBLE
    }
}