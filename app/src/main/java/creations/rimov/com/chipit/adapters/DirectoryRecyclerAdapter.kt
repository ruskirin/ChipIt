package creations.rimov.com.chipit.adapters

import android.content.Context
import android.view.*
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipCard

/**
 * TODO: add Glide Recyclerview integration if scrolling causes stuttering
 */
class DirectoryRecyclerAdapter(private val context: Context,
                               private val touchHandler: DirectoryAdapterHandler)
    : RecyclerView.Adapter<DirectoryRecyclerAdapter.DirectoryViewHolder>() {

    private lateinit var topics: List<ChipCard>
    //Reference to selected ViewHolder
    private lateinit var selectedTopic: DirectoryViewHolder

    init {
        //Adapter does not return proper id from overriden #getItemId() otherwise
        setHasStableIds(true)
    }

    fun setTopics(topics: List<ChipCard>) {
        this.topics = topics

        notifyDataSetChanged()
    }

    fun getSelectedId() =
        if(::selectedTopic.isInitialized)
            selectedTopic.itemId
        else -1L

    fun isEditing() = selectedTopic.isEditing()

    fun toggleEditing() {
        selectedTopic.toggleEditing()
    }

    /**
     * VIEW HOLDER
     * @param: touchHandler = interface for communicating touched events' information to activity
     */
    inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {

        val layout: LinearLayout = itemView.findViewById(R.id.dirRecyclerTopicLayout)
        val image: ImageView = itemView.findViewById(R.id.dirRecyclerTopicImage)
        val description: TextView = itemView.findViewById(R.id.dirRecyclerTopicDescription)

        private val editLayout: LinearLayout = itemView.findViewById(R.id.dirRecyclerEditLayout)
        private val editButtonImage: Button = itemView.findViewById(R.id.dirRecyclerEditImage)
        private val editButtonDesc: Button = itemView.findViewById(R.id.dirRecyclerEditDesc)
        private val editButtonDelete: Button = itemView.findViewById(R.id.dirRecyclerEditDelete)

        init {
            layout.setOnTouchListener(this)

            editButtonImage.setOnTouchListener(this)
            editButtonDesc.setOnTouchListener(this)
            editButtonDelete.setOnTouchListener(this)
        }

        private fun setSelectedTopic(topic: DirectoryViewHolder) {

            if(!::selectedTopic.isInitialized)
                selectedTopic = topic

            if(selectedTopic.itemId != itemId) {

                if(selectedTopic.isEditing())
                    selectedTopic.toggleEditing()

                selectedTopic = topic
            }
        }

        fun isEditing() = editLayout.isVisible
        //Toggle visibility of editLayout
        fun toggleEditing() {

            if(this.isEditing()) {
                editLayout.visibility = View.GONE

            } else
                editLayout.visibility = View.VISIBLE
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {

            if(event == null)
                return false

            val id = itemId

            when(view?.id) {
                R.id.dirRecyclerTopicLayout -> {

                    if(event.action == MotionEvent.ACTION_DOWN)
                        setSelectedTopic(this)

                    touchHandler.topicTouch(id, event)
                }

                R.id.dirRecyclerEditImage -> {
                    touchHandler.topicEditImage(id, event)
                }

                R.id.dirRecyclerEditDesc -> {


                }

                R.id.dirRecyclerEditDelete -> {

                    if(event.action == MotionEvent.ACTION_UP)
                        touchHandler.topicDelete(id)
                }
            }

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

    //TODO (IMPORTANT): might be a memory leak with TextView here, check logs for the message, then follow:
    //                   https://stackoverflow.com/questions/49513726/android-memory-leak-with-fragments
    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {

        holder.description.text = topics[position].description
        Glide.with(context)
            .load(topics[position].imgLocation)
            .into(holder.image)
    }

    interface DirectoryAdapterHandler {

        fun topicTouch(id: Long, event: MotionEvent)

        fun topicEditImage(id: Long, event: MotionEvent)

        fun topicEditDesc(id: Long, text: String)

        fun topicDelete(id: Long)
    }
}