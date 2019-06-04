package creations.rimov.com.chipit.adapters

import android.content.Context
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.util.handlers.RecyclerTouchHandler

/**
 * TODO: add Glide Recyclerview integration if scrolling causes stuttering
 */
class DirectoryRecyclerAdapter(private val context: Context,
                               private val touchHandler: RecyclerTouchHandler)
    : RecyclerView.Adapter<DirectoryRecyclerAdapter.DirectoryViewHolder>() {

    private lateinit var topics: List<Chip>

    init {
        //Adapter does not return proper id from overriden #getItemId() otherwise
        setHasStableIds(true)
    }

    fun setTopics(topics: List<Chip>) {
        this.topics = topics

        notifyDataSetChanged()
    }

    /**
     * VIEW HOLDER
     * @param: touchHandler = interface for communicating touch events' information to activity
     */
    inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {

        val topicName: TextView = itemView.findViewById(R.id.directory_recycler_topic_name)
        val topicImage: ImageView = itemView.findViewById(R.id.directory_recycler_topic_image)

        init {
            itemView.setOnTouchListener(this)
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {

            if(event == null)
                return false

            touchHandler.topicTouch(adapterPosition, itemId, event)
            Log.i("Touch Event", "DirectoryRecyclerAdapter#onTouch(): pos: $adapterPosition, topic id: ${topics[adapterPosition].id}, returned id: $itemId")
            view?.performClick()

            return true
        }
    }

    override fun getItemCount(): Int {
        if(::topics.isInitialized)
            return topics.size

        return 0
    }

    override fun getItemId(position: Int) = topics[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
        val chipHolder =
            LayoutInflater.from(context).inflate(R.layout.directory_recycler_chip_layout, parent, false)

        return DirectoryViewHolder(chipHolder)
    }

    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {

        holder.topicName.text = topics[position].name
        Glide.with(context)
            .load(topics[position].imgLocation)
            .into(holder.topicImage)
    }
}