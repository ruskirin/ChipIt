package creations.rimov.com.chipit.adapters.viewholders.web

import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import creations.rimov.com.chipit.adapters.ViewHolderHandler
import creations.rimov.com.chipit.database.objects.ChipCard
import kotlinx.android.synthetic.main.recycler_web_card_image.view.*
import kotlinx.android.synthetic.main.recycler_web_card_viewgroup.view.*

class WebViewHolderImage(itemView: View)
    : WebViewHolder(itemView),
      View.OnTouchListener {

    val image: ImageView = itemView.cardImage
    override val name: TextView = itemView.cardName
    override val desc: TextView = itemView.cardDesc
    override val counter: TextView = itemView.cardCounter
    override val btnEdit: Button = itemView.btnCardEdit

    override var isEditing: Boolean = btnEdit.isVisible

    init {
        itemView.setOnTouchListener(this)
    }

    /**
     * @param handler
     * @param opts[0]: chip to display in viewholder
     */
    override fun prepare(
      handler: ViewHolderHandler, vararg opts: Any?) {

        this.handler = handler

        opts[0]?.let {
            if(it !is ChipCard)
                throw IllegalArgumentException("No ChipCard passed to ::prepare()")

            displayChip(it)
        }
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        when(event?.action) {
            MotionEvent.ACTION_DOWN -> handler.handleGesture(event, this)
            else -> handler.handleGesture(event)
        }

        return true
    }

    override fun displayChip(chip: ChipCard) {

        Glide.with(image.context)
            .load(chip.matPath)
            .apply(RequestOptions()
                       .override(image.width, image.height))
            .into(image)

        name.text = chip.name
        desc.text = chip.desc
        counter.text = chip.numChildren.toString()
    }
}