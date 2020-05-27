package creations.rimov.com.chipit.adapters.viewholders.web

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import creations.rimov.com.chipit.adapters.ViewHolderHandler
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.viewgroups.CustomView

abstract class WebViewHolder(itemView: View)
    : RecyclerView.ViewHolder(itemView),
      CustomView<ViewHolderHandler> {

    open lateinit var handler: ViewHolderHandler

    abstract val name: TextView
    abstract val desc: TextView
    abstract val counter: TextView
    abstract val btnEdit: Button

    abstract var isEditing: Boolean

    abstract fun toggleEdit(edit: Boolean)

    abstract fun toggleDetail()
}