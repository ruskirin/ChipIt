package creations.rimov.com.chipit.ui.web.adapters.viewholders.web

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.util.constants.EditorConsts
import creations.rimov.com.chipit.ui.web.adapters.ViewHolderHandler
import creations.rimov.com.chipit.data.objects.ChipCard
import creations.rimov.com.chipit.extension.gone
import creations.rimov.com.chipit.extension.visible
import creations.rimov.com.chipit.ui.custom.CustomView
import kotlinx.android.synthetic.main.recycler_web_card_detail.view.*
import kotlinx.android.synthetic.main.recycler_web_card_title.view.*

abstract class WebViewHolder(itemView: View)
    : RecyclerView.ViewHolder(itemView), CustomView<ViewHolderHandler> {

    override lateinit var handler: ViewHolderHandler

    private val name: TextView? by lazy {itemView.cardName}
    private val desc: TextView? by lazy {itemView.cardDesc}
    private val counter: TextView? by lazy {itemView.cardCounter}

    private val btnExpand: Button? by lazy {itemView.cardBtnExpand}

    abstract var isEditing: Boolean

    //TODO ANIMATE
    fun toggleDetail() {

        desc?.let {
            if(it.isVisible) {
                desc.gone()

                btnExpand?.setCompoundDrawablesWithIntrinsicBounds(
                  null,
                  btnExpand?.resources
                      ?.getDrawable(R.drawable.ic_arrow_down, null),
                  null, null)
            }
            else {
                desc.visible()

                btnExpand?.setCompoundDrawablesWithIntrinsicBounds(
                  null,
                  btnExpand?.resources
                      ?.getDrawable(R.drawable.ic_arrow_up, null),
                  null, null)
            }
        }
    }

    @CallSuper
    open fun displayChip(chip: ChipCard) {

        desc?.text = chip.desc
        counter?.text = chip.numChildren.toString()

        name?.apply {
            text = chip.name

            when(chip.matType) {
                EditorConsts.IMAGE ->
                    setCompoundDrawablesWithIntrinsicBounds(
                      resources.getDrawable(R.drawable.ic_addimage, null),
                      null, null, null)
                EditorConsts.VIDEO ->
                    setCompoundDrawablesWithIntrinsicBounds(
                      resources.getDrawable(R.drawable.ic_addvideo, null),
                      null, null, null)
                EditorConsts.AUDIO ->
                    setCompoundDrawablesWithIntrinsicBounds(
                      resources.getDrawable(R.drawable.ic_addaudio, null),
                      null, null, null)
                EditorConsts.TEXT ->
                    setCompoundDrawablesWithIntrinsicBounds(
                      resources.getDrawable(R.drawable.ic_addtext, null),
                      null, null, null)
            }
        }

        btnExpand?.setOnClickListener {
            toggleDetail()
        }
    }
}