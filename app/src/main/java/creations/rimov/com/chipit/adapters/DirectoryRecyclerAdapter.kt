package creations.rimov.com.chipit.adapters

import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.Topic
import creations.rimov.com.chipit.util.handlers.RecyclerHandler

/**
 * TODO: add Glide Recyclerview integration if scrolling causes stuttering
 */
class DirectoryRecyclerAdapter(private val context: Context,
                               private val touchHandler: RecyclerHandler)
    : RecyclerView.Adapter<DirectoryRecyclerAdapter.DirectoryViewHolder>() {

    private lateinit var topics: List<Topic>

    fun setTopics(topics: List<Topic>) {
        this.topics = topics

        notifyDataSetChanged()
    }

    /**
     * VIEW HOLDER
     * @param: touchHandler = interface for communicating touch events' information to activity
     */
    class DirectoryViewHolder(itemView: View, private val touchHandler: RecyclerHandler)
        : RecyclerView.ViewHolder(itemView), View.OnTouchListener {

        val topicName: TextView = itemView.findViewById(R.id.directory_recycler_topic_name)
        val topicImage: ImageView = itemView.findViewById(R.id.directory_recycler_topic_image)

        init {
            itemView.setOnTouchListener(this)
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {

            if(event != null) {
                touchHandler.topicTouch(adapterPosition, event)

                if(event.action == MotionEvent.ACTION_UP)
                    view?.performClick()

                return true
            }

            return false
        }
    }

    override fun getItemCount(): Int {
        if(::topics.isInitialized)
            return topics.size

        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
        val chipHolder =
            LayoutInflater.from(context).inflate(R.layout.directory_recycler_chip_layout, parent, false)

        return DirectoryViewHolder(chipHolder, touchHandler)
    }

    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {

        holder.topicName.text = topics[position].name
        Glide.with(context)
            .load(topics[position].imagePath)
            .into(holder.topicImage)
    }
}