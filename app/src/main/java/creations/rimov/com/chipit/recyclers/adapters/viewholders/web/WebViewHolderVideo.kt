package creations.rimov.com.chipit.recyclers.adapters.viewholders.web

import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import creations.rimov.com.chipit.recyclers.adapters.ViewHolderHandler
import creations.rimov.com.chipit.database.objects.ChipCard
import kotlinx.android.synthetic.main.recycler_web_card_video.view.*

class WebViewHolderVideo(itemView: View)
    : WebViewHolder(itemView),
      View.OnTouchListener {

    val thumbnail: ImageView = itemView.cardThumbnail

    override var isEditing: Boolean = false

    override fun prepare(handler: ViewHolderHandler, vararg opts: Any?) {

        this.handler = handler

        opts[0]?.let {
            if(it !is ChipCard)
                throw IllegalArgumentException("No ChipCard passed to ::prepare()")

            displayChip(it)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        when(event?.action) {
            MotionEvent.ACTION_DOWN -> handler.handleGesture(event, this)
            else -> handler.handleGesture(event)
        }

        return true
    }

    override fun displayChip(chip: ChipCard) {
        super.displayChip(chip)

        Glide.with(thumbnail.context)
            .asBitmap()
            .load(chip.matPath)
            .apply(
              RequestOptions()
                       .override(thumbnail.width, thumbnail.height))
            .into(thumbnail)
    }
}