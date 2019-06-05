package creations.rimov.com.chipit.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
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
    private val prevChip: TempChip = TempChip()

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
        val chipButtonDelete: ImageButton = itemView.findViewById(R.id.web_recycler_chip_delete)

        init {
            chipImage.setOnTouchListener(this)
            chipButtonDelete.setOnTouchListener(this)
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {

            if(event == null)
                return false

            if(listType == WebFragment.ListType.LOWER) {
                touchHandler.topicTouch(adapterPosition, itemId, event, listType)

                return true
            }

            //TODO NOW: occasionally an ArrayIndexOutOfBoundsException is thrown when deleting, triggered at this location; fix
            if(view?.id == R.id.web_recycler_chip_delete) {
                touchHandler.topicDelete(getItemId(adapterPosition))

                Log.i("Touch Event", "WebRecyclerAdapter#onTouch(): set chip ${getItemId(adapterPosition)} for deletion")

                return true
            }

            //Visual changes to indicate selection
            if(event.action == MotionEvent.ACTION_UP) {
                val chipId = getItemId(adapterPosition)

                //Selected chip is different from previous
                if(chipId != prevChip.getId()) {
                    //Undo the changes to the previous chip
                    prevChip.resetClickMod()
                    //Save information about selected chip
                    prevChip.assignValues(chipId, chipImage, chipButtonDelete)
                    //Modify chip to indicate selection
                    prevChip.clickMod()
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

    /**Hold information about the previously modified ChipImageHolder to allow for any visual modifications**/
    class TempChip(
        private var chipId: Long = -1L,
        private var chipImage: ImageView? = null,
        private var chipButton: ImageButton? = null) {

        fun getId() = chipId

        fun assignValues(chipId: Long, chipImage: ImageView, chipButton: ImageButton) {
            this.chipId = chipId
            this.chipImage = chipImage
            this.chipButton = chipButton
        }

        fun resetClickMod() {
            chipImage?.alpha = 1.0f
            chipButton?.visibility = View.GONE
        }

        fun clickMod() {
            chipImage?.alpha = 0.5f
            chipButton?.visibility = View.VISIBLE
        }
    }
}