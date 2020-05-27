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
import creations.rimov.com.chipit.extensions.gone
import creations.rimov.com.chipit.extensions.visible
import kotlinx.android.synthetic.main.recycler_web_card_image.view.*

class WebViewHolderImage(itemView: View)
    : WebViewHolder(itemView),
      View.OnTouchListener {

    val content: ImageView = itemView.cardImage
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

    //TODO ANIMATE
    override fun toggleEdit(edit: Boolean) {

        if(edit) btnEdit.visible()
        else btnEdit.gone()
    }

    //TODO ANIMATE
    override fun toggleDetail() {

        if(desc.isVisible) desc.gone()
        else desc.visible()
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        when(event?.action) {
            MotionEvent.ACTION_DOWN -> handler.handleGesture(event, this)
            else -> handler.handleGesture(event)
        }

        return true
    }

    private fun displayChip(chip: ChipCard) {

        Glide.with(content.context)
            .load(chip.matPath)
            .apply(RequestOptions()
                       .override(content.width, content.height))
            .into(content)

        name.text = chip.name
        desc.text = chip.desc
        counter.text = chip.numChildren.toString()
    }
}