package creations.rimov.com.chipit.adapters

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.viewgroups.CardLayout
import creations.rimov.com.chipit.viewgroups.CardEditorLayout
import kotlinx.android.synthetic.main.recycler_web_chip.view.*

//TODO (FUTURE): images can be linked through either a file path or as bitmap, both have pros and cons

class WebRecyclerAdapter(private val touchHandler: Handler)
    : RecyclerView.Adapter<WebRecyclerAdapter.WebViewHolder>() {

    private lateinit var chips: List<ChipCard>
    //Reference to the touched chip
    lateinit var selectedChip: WebViewHolder

    init {
        //Adapter does not return proper id from overriden #getItemId() otherwise
        setHasStableIds(true)
    }

    fun setChips(chips: List<ChipCard>) {
        this.chips = chips

        notifyDataSetChanged()
    }

    fun getSelectedId() =
        if(::selectedChip.isInitialized)
            selectedChip.itemId
        else -1L

    /**
     * VIEW HOLDER
     */
    inner class WebViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {

        val cardLayout: CardLayout = itemView.webRecyclerCard
        val editLayout: CardEditorLayout = itemView.webRecyclerEditor

        init {
            cardLayout.setOnTouchListener(this)
            editLayout.setTouchListener(this)
        }

        private fun setSelectedChip(chip: WebViewHolder) {

            if(!::selectedChip.isInitialized) selectedChip = chip

            if(selectedChip.itemId == itemId) return

            selectedChip.edit(false)

            selectedChip = chip
        }

        fun isEditing() = editLayout.isVisible

        fun edit(edit: Boolean) {

            if(edit) editLayout.show(true)
            else editLayout.hide()
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {

            if (event == null)
                return false

            when(view?.id) {
                R.id.webRecyclerCard -> {
                    if(event.action == MotionEvent.ACTION_DOWN)
                        setSelectedChip(this)

                    touchHandler.chipTouch(event)
                }
                R.id.cardEditorBtnDelete -> {
                    if(event.action == MotionEvent.ACTION_UP)
                        touchHandler.chipDelete(chips[adapterPosition].id)
                }
            }

            view?.performClick()
            return true
        }
    }

    override fun getItemCount() =
        if (::chips.isInitialized) chips.size
        else 0

    override fun getItemId(position: Int) = chips[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebViewHolder {

        val chipHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_web_chip, parent, false)

        return WebViewHolder(chipHolder)
    }

    override fun onBindViewHolder(holder: WebViewHolder, position: Int) {

        holder.cardLayout.setChip(chips[position])
    }


    interface Handler {

        fun chipTouch(event: MotionEvent)

        fun chipDelete(id: Long)
    }
}