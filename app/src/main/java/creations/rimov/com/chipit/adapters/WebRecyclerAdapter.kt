package creations.rimov.com.chipit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipCard
import kotlinx.android.synthetic.main.card_layout.view.*

//TODO (FUTURE): images can be linked through either a file path or as bitmap, both have pros and cons

class WebRecyclerAdapter(
    private val context: Context,
    private val touchHandler: AlbumAdapterHandler) : RecyclerView.Adapter<WebRecyclerAdapter.ChipViewHolder>() {

    private lateinit var chips: List<ChipCard>
    //Reference to the touched chip
    private lateinit var selectedChip: ChipViewHolder

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

    fun isEditing() = selectedChip.isEditing()

    fun toggleEditing() {

        if (::selectedChip.isInitialized)
            selectedChip.toggleEditing()
    }

    /**
     * VIEW HOLDER
     */
    inner class ChipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {

        val image: ImageView = itemView.cardImage
        val topic: TextView = itemView.cardName

        private val editLayout: LinearLayout = itemView.cardRecyclerEditLayout

        init {
            itemView.albumRecyclerChipLayout.setOnTouchListener(this)

            itemView.albumRecyclerBtnExpand.setOnTouchListener(this)
            itemView.albumRecyclerEditImage.setOnTouchListener(this)
            itemView.cardRecyclerBtnEdit.setOnTouchListener(this)
            itemView.cardRecyclerBtnDelete.setOnTouchListener(this)
        }

        private fun setSelectedChip(chip: ChipViewHolder) {

            if(!::selectedChip.isInitialized)
                selectedChip = chip

            if(selectedChip.itemId != itemId) {

                if(selectedChip.isEditing())
                    selectedChip.toggleEditing()

                selectedChip = chip
            }
        }

        fun isEditing() = selectedChip.editLayout.isVisible

        fun toggleEditing() {

            if (selectedChip.isEditing())
                selectedChip.editLayout.visibility = View.GONE
            else
                selectedChip.editLayout.visibility = View.VISIBLE
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {

            if (event == null)
                return false

            val id = itemId

            when (view?.id) {

                R.id.albumRecyclerChipLayout -> {

                    if(event.action == MotionEvent.ACTION_DOWN)
                        setSelectedChip(this)

                    touchHandler.topicTouch(id, event)
                }

                R.id.albumRecyclerBtnExpand -> {

                    if(event.action == MotionEvent.ACTION_UP)
                        touchHandler.topicExpand(id)
                }

                R.id.albumRecyclerEditImage -> {

                }

                R.id.cardRecyclerBtnEdit -> {

                }

                R.id.cardRecyclerBtnDelete -> {

                    if(event.action == MotionEvent.ACTION_UP)
                        touchHandler.topicDelete(id)
                }
            }

            view?.performClick()
            return true
        }
    }

    override fun getItemCount() =
        if (::chips.isInitialized)
            chips.size
        else
            0

    override fun getItemId(position: Int) = chips[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {

        val chipHolder = LayoutInflater.from(context)
            .inflate(R.layout.card_layout, parent, false)

        return ChipViewHolder(chipHolder)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {

        holder.topic.text = chips[position].name
        //TODO: load a default image if none can be found
        Glide.with(context)
            .load(chips[position].imgLocation)
            .into(holder.image)
    }

    interface AlbumAdapterHandler {

        fun topicTouch(id: Long, event: MotionEvent)

        fun topicExpand(id: Long)

        fun topicDelete(id: Long)
    }
}