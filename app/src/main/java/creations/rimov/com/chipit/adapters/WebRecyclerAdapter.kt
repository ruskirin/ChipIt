package creations.rimov.com.chipit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.viewgroups.CardLayout
import creations.rimov.com.chipit.viewgroups.ChipEditorLayout
import kotlinx.android.synthetic.main.web_recycler_chip_layout.view.*

//TODO (FUTURE): images can be linked through either a file path or as bitmap, both have pros and cons

class WebRecyclerAdapter(
    private val context: Context,
    private val touchHandler: WebAdapterHandler) : RecyclerView.Adapter<WebRecyclerAdapter.WebViewHolder>() {

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
        val editLayout: ChipEditorLayout = itemView.webRecyclerEditor

        init {
            cardLayout.setOnTouchListener(this)
            editLayout.setTouchListener(this)
        }

        private fun setSelectedChip(chip: WebViewHolder) {

            if(!::selectedChip.isInitialized)
                selectedChip = chip

            if(selectedChip.itemId == itemId)
                return

            selectedChip.edit(false)

            selectedChip = chip
        }

        fun isEditing() = editLayout.isVisible

        fun edit(edit: Boolean) {
            editLayout.show(edit)
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {

            if (event == null)
                return false

            val id = itemId

            when (view?.id) {

                R.id.webRecyclerCard -> {

                    if(event.action == MotionEvent.ACTION_DOWN)
                        setSelectedChip(this)

                    touchHandler.chipTouch(event)
                }

                R.id.chipEditorBtnDelete -> {

                    if(event.action == MotionEvent.ACTION_UP)
                        touchHandler.chipDelete(chips[adapterPosition])
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

        val chipHolder = LayoutInflater.from(context)
            .inflate(R.layout.card_layout, parent, false)

        return WebViewHolder(chipHolder)
    }

    override fun onBindViewHolder(holder: WebViewHolder, position: Int) {

        holder.cardLayout.setChip(chips[position])
    }

    interface WebAdapterHandler {

        fun chipTouch(event: MotionEvent)

        fun chipEdit(chip: ChipIdentity)

        fun chipDelete(chip: ChipCard)
    }
}