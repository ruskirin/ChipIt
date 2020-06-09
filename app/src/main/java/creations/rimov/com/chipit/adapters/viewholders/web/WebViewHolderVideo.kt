package creations.rimov.com.chipit.adapters.viewholders.web

import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import creations.rimov.com.chipit.adapters.ViewHolderHandler
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.viewgroups.custom.CustomVideoLayout
import kotlinx.android.synthetic.main.recycler_web_card_video.view.*
import kotlinx.android.synthetic.main.recycler_web_card_viewgroup.view.*

class WebViewHolderVideo(itemView: View)
    : WebViewHolder(itemView),
      View.OnTouchListener {

    val video: CustomVideoLayout = itemView.cardVideo
    override val name: TextView = itemView.cardName
    override val desc: TextView = itemView.cardDesc
    override val counter: TextView = itemView.cardCounter
    override val btnEdit: Button = itemView.btnCardEdit

    override var isEditing: Boolean = btnEdit.isVisible

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

        name.text = chip.name
        desc.text = chip.desc
        counter.text = chip.numChildren.toString()
    }
}