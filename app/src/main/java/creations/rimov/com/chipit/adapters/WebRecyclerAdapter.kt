package creations.rimov.com.chipit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.fragments.WebFragment
import creations.rimov.com.chipit.util.handlers.RecyclerTouchHandler

//TODO (FUTURE): images can be linked through either a file path or as bitmap, both have pros and cons

class WebRecyclerAdapter(private val context: Context,
                         private val listType: Int,
                         private val touchHandler: RecyclerTouchHandler)
    : RecyclerView.Adapter<WebRecyclerAdapter.ChipImageHolder>() {

    private lateinit var chips: List<ChipCard>

    //Keep track of previously selected view to undo any visual changes
    private var prevChipId = -1L
    private var prevChip: View? = null

    init {
        //Adapter does not return proper id from overriden #getItemId() otherwise
        setHasStableIds(true)
    }

    fun setChips(chips: List<ChipCard>) {
        this.chips = chips

        notifyDataSetChanged()
    }

    /**
     * VIEW HOLDER
     */
    inner class ChipImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {
        val chipImage: ImageView = itemView.findViewById(R.id.web_recycler_chip_image)

        init {
            itemView.setOnTouchListener(this)
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {

            if(event == null)
                return false

            //Change the alpha value of the pressed view to show selection
            if(listType == WebFragment.ListType.UPPER && event.action == MotionEvent.ACTION_UP) {
                val chipId = getItemId(adapterPosition)

                if(chipId != prevChipId) {
                    prevChip?.let {
                        it.alpha = 1.0f
                    }

                    prevChipId = getItemId(adapterPosition)
                    prevChip = view
                    view?.alpha = 0.5f
                }
            }

            touchHandler.topicTouch(adapterPosition, itemId, event, listType)

            view?.performClick()

            return true
        }
    }

    override fun getItemCount(): Int {

        if(::chips.isInitialized)
            return chips.size

        return 0
    }

    override fun getItemId(position: Int) = chips[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipImageHolder {

        val chipHolder = LayoutInflater.from(context)
            .inflate(R.layout.web_recycler_chip_layout, parent, false)

        return ChipImageHolder(chipHolder)
    }

    override fun onBindViewHolder(holder: ChipImageHolder, position: Int) {

        //TODO: load a default image if none can be found
        Glide.with(context)
            .load(chips[position].imgLocation)
            .into(holder.chipImage)
    }
}