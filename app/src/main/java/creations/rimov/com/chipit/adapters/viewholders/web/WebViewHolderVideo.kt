package creations.rimov.com.chipit.adapters.viewholders.web

import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import creations.rimov.com.chipit.adapters.ViewHolderHandler
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.viewgroups.CustomView

class WebViewHolderVideo(itemView: View)
    : WebViewHolder(itemView),
      View.OnTouchListener {

    override val name: TextView
        get() = TODO("Not yet implemented")

    override val desc: TextView
        get() = TODO("Not yet implemented")

    override val counter: TextView
        get() = TODO("Not yet implemented")

    override val btnEdit: Button
        get() = TODO("Not yet implemented")

    override var isEditing: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun prepare(handler: ViewHolderHandler, vararg opts: Any?) {

    }

    override fun toggleEdit(edit: Boolean) {
        TODO("Not yet implemented")
    }

    override fun toggleDetail() {
        TODO("Not yet implemented")
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {


        return false
    }

    private fun displayChip(chip: ChipCard) {
        TODO("Not yet implemented")
    }
}