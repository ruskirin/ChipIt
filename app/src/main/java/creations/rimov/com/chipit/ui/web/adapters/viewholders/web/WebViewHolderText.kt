package creations.rimov.com.chipit.ui.web.adapters.viewholders.web

import android.view.MotionEvent
import android.view.View
import creations.rimov.com.chipit.ui.web.adapters.ViewHolderHandler
import creations.rimov.com.chipit.data.objects.ChipCard

class WebViewHolderText(itemView: View)
    : WebViewHolder(itemView),
      View.OnTouchListener {


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
    }
}