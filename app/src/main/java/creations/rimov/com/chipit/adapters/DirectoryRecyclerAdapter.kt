package creations.rimov.com.chipit.adapters

import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.util.handlers.RecyclerHandler
import creations.rimov.com.chipit.objects.Subject

/**
 * TODO: add Glide Recyclerview integration if scrolling causes stuttering
 */
class DirectoryRecyclerAdapter(private val context: Context,
                               private val subjects: List<Subject>,
                               private val touchHandler: RecyclerHandler)
    : RecyclerView.Adapter<DirectoryRecyclerAdapter.DirectoryViewHolder>() {

    override fun getItemCount(): Int = subjects.size

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
        val chipHolder =
            LayoutInflater.from(context).inflate(R.layout.directory_recycler_chip_layout, parent, false)

        return DirectoryViewHolder(
            chipHolder,
            touchHandler
        )
    }

    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {

        holder.topicName.text = subjects[position].name
        Glide.with(context)
            .load(subjects[position].imagePath)
            .into(holder.topicImage)
    }
}