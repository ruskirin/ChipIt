package creations.rimov.com.chipit.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.util.handlers.RecyclerTouchHandler

//TODO: (FUTURE) images can be linked through either a file path or as bitmap, both have pros and cons

class WebRecyclerAdapter(private val context: Context,
                         private val listType: Int,
                         private val touchHandler: RecyclerTouchHandler)
    : RecyclerView.Adapter<WebRecyclerAdapter.ChipImageHolder>() {

    private lateinit var chips: List<ChipCard>


    fun setChips(chips: List<ChipCard>) {
        this.chips = chips

        notifyDataSetChanged()
    }

    /**
     * VIEW HOLDER
     */
    inner class ChipImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {

        val chipImage: ImageView = itemView.findViewById(R.id.web_layout_recycler_chip_image)

        init {
            itemView.setOnTouchListener(this)
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {

            if(event?.action == MotionEvent.ACTION_UP) {
                touchHandler.topicTouch(adapterPosition, chips[adapterPosition].id, event, listType)

                view?.performClick()

                return true
            }

            return false
        }
    }

    override fun getItemCount(): Int {

        if(::chips.isInitialized)
            return chips.size

        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipImageHolder {

        val chipHolder = LayoutInflater.from(context)
            .inflate(R.layout.web_recycler_chip_layout, parent, false)

        return ChipImageHolder(chipHolder)
    }

    override fun onBindViewHolder(holder: ChipImageHolder, position: Int) {

        //TODO: (QUICK) load a default image if none can be found
        Glide.with(context)
            .load(chips[position].imgLocation)
            .into(holder.chipImage)
    }
}