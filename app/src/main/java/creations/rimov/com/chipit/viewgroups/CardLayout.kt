package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.FlexboxLayoutManager
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipCard
import kotlinx.android.synthetic.main.card_layout.view.*

class CardLayout(context: Context, attrs: AttributeSet) : CardView(context, attrs) {

    private val image: ImageView by lazy {cardImage}
    private val name: TextView by lazy {cardName}
    private val counter: TextView by lazy{cardCounter}


    init {
        View.inflate(context, R.layout.card_layout, this)
    }

    fun setChip(chip: ChipCard) {

        name.text = chip.name
        counter.text = chip.counter.toString()
        Glide.with(image.context)
            .load(chip.imgLocation)
            .apply(RequestOptions()
                .override(image.width, image.height))
            .into(image)
    }
}