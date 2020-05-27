package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipCard
import kotlinx.android.synthetic.main.recycler_web_card_image.view.*

class CardImageLayout(context: Context, attrs: AttributeSet)
    : ConstraintLayout(context, attrs) {

    private val image: ImageView by lazy {cardImage}
    private val name: TextView by lazy {cardName}
    private val desc: TextView by lazy {cardDesc}
    private val counter: TextView by lazy{cardCounter}

    init {
        View.inflate(context, R.layout.recycler_web_card_image, this)
    }

    fun setChip(chip: ChipCard) {

        name.text = chip.name
        counter.text = chip.numChildren.toString()
        Glide.with(image.context)
            .load(chip.matPath)
            .apply(RequestOptions()
                .override(image.width, image.height))
            .into(image)
    }
}