package creations.rimov.com.chipit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipCard
import kotlinx.android.synthetic.main.topic_chip_layout.view.*

class TopicChipAdapter(private val context: Context) : RecyclerView.Adapter<TopicChipAdapter.ChipViewHolder>() {

    private lateinit var children: List<ChipCard>

    init {
        //Adapter does not return proper id from overriden #getItemId() otherwise
        setHasStableIds(true)
    }

    fun setChildren(children: List<ChipCard>) {
        this.children = children

        notifyDataSetChanged()
    }

    /**
     * VIEW HOLDER
     */
    inner class ChipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {

        val image: ImageView = itemView.topicChipImage
        val name: TextView = itemView.topicChipName

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {

            if (event == null)
                return false

            val id = itemId

            when (view?.id) {

            }

            view?.performClick()
            return true
        }
    }

    override fun getItemCount() =
        if (::children.isInitialized) children.size
        else 0

    override fun getItemId(position: Int) = children[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {

        val chipHolder = LayoutInflater.from(context)
            .inflate(R.layout.topic_chip_layout, parent, false)

        return ChipViewHolder(chipHolder)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {

        holder.name.text = children[position].name
        //TODO: load a default image if none can be found
        Glide.with(context)
            .load(children[position].imgLocation)
            .into(holder.image)
    }
}