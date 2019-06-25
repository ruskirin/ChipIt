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
import creations.rimov.com.chipit.database.objects.ChipPath
import kotlinx.android.synthetic.main.chipper_recycler_chip_layout.view.*

class ChipperRecyclerAdapter(
    private val context: Context,
    private val touchHandler: WebAdapterHandler) : RecyclerView.Adapter<ChipperRecyclerAdapter.ChipperViewHolder>() {

    private lateinit var chips: List<ChipPath>

    init {
        //Adapter does not return proper id from overriden #getItemId() otherwise
        setHasStableIds(true)
    }

    fun setTopics(chips: List<ChipPath>) {
        this.chips = chips

        notifyDataSetChanged()
    }

    //TODO NOW: ImageView below throws IllegalStateException
    inner class ChipperViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnTouchListener {

        val image: ImageView = itemView.chipperRecyclerImage.apply {
            setOnTouchListener(this@ChipperViewHolder)
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {

            if(event == null)
                return false

            if(event.action == MotionEvent.ACTION_UP)
                touchHandler.chipTouch(itemId)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipperViewHolder {
        val chipHolder =
            LayoutInflater.from(context).inflate(R.layout.chipper_recycler_chip_layout, parent, false)

        return ChipperViewHolder(chipHolder)
    }

    //TODO (IMPORTANT): might be a memory leak with TextView here, check logs for the message, then follow:
    //                   https://stackoverflow.com/questions/49513726/android-memory-leak-with-fragments
    override fun onBindViewHolder(holder: ChipperViewHolder, position: Int) {

        Glide.with(context)
            .load(chips[position].imgLocation)
            .into(holder.image)
    }

    interface WebAdapterHandler {

        fun chipTouch(id: Long)
    }
}