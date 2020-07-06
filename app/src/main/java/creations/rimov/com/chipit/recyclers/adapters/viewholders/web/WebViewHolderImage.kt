package creations.rimov.com.chipit.recyclers.adapters.viewholders.web

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.recyclers.adapters.ViewHolderHandler
import creations.rimov.com.chipit.database.objects.ChipCard
import kotlinx.android.synthetic.main.recycler_web_card_image.view.*

class WebViewHolderImage(itemView: View)
    : WebViewHolder(itemView),
      View.OnTouchListener {

    val image: ImageView = itemView.cardImage

    override var isEditing: Boolean = false

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
            MotionEvent.ACTION_DOWN ->
                handler.handleGesture(event, this)
            MotionEvent.ACTION_UP ->
                if(view?.id == R.id.cardBtnExpand) toggleDetail()
            else ->
                handler.handleGesture(event)
        }

        return true
    }

    override fun displayChip(chip: ChipCard) {
        super.displayChip(chip)

        Glide.with(image.context)
            .load(chip.matPath)
            .apply(RequestOptions()
                       .override(image.width, image.height))
            .into(image)
    }
}