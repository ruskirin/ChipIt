package creations.rimov.com.chipit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.util.handlers.RecyclerHandler

//TODO: (FUTURE) images can be linked through either a file path or as bitmap, both have pros and cons

class WebRecyclerAdapter(private val context: Context,
                         private val listType: Int,
                         private val touchHandler: RecyclerHandler)
    : RecyclerView.Adapter<WebRecyclerAdapter.ChipImageHolder>() {

    private lateinit var chips: List<Chip>


    fun setChips(chips: List<Chip>) {
        this.chips = chips

        notifyDataSetChanged()
    }

    /**
     * VIEW HOLDER
     */
    class ChipImageHolder(private val listType: Int,
                          itemView: View,
                          private val touchHandler: RecyclerHandler)
        : RecyclerView.ViewHolder(itemView), View.OnTouchListener {

        val chipImage: ImageView = itemView.findViewById(R.id.web_layout_recycler_chip_image)

        init {
            itemView.setOnTouchListener(this)
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {

            if(event != null) {
                touchHandler.topicTouch(adapterPosition, event, listType)

                if(event.action == MotionEvent.ACTION_UP)
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

        return ChipImageHolder(listType, chipHolder, touchHandler)
    }

    override fun onBindViewHolder(holder: ChipImageHolder, position: Int) {

        //TODO: (QUICK) load a default image if none can be found
        Glide.with(context)
            .load(chips[position].imagePath)
            .into(holder.chipImage)
    }
}