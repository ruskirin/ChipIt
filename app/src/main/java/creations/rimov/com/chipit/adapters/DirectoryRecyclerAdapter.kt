package creations.rimov.com.chipit.adapters

import android.content.Context
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipCard

/**
 * TODO: add Glide Recyclerview integration if scrolling causes stuttering
 */
class DirectoryRecyclerAdapter(private val context: Context,
                               private val touchHandler: DirectoryAdapterHandler
)
    : RecyclerView.Adapter<DirectoryRecyclerAdapter.DirectoryViewHolder>() {

    private lateinit var topics: List<ChipCard>
    //Reference to the edit layout to allow triggering of visibility depending on event action
    private val selectedTopic = SelectedTopic()

    init {
        //Adapter does not return proper id from overriden #getItemId() otherwise
        setHasStableIds(true)
    }

    fun setTopics(topics: List<ChipCard>) {
        this.topics = topics

        notifyDataSetChanged()
    }

    fun isEditVisible() = selectedTopic.isEditVisible()

    fun setEditVisibility(visible: Boolean) {

        if(visible)
            selectedTopic.setSelected()
        else
            selectedTopic.setUnselected()
    }

    /**
     * VIEW HOLDER
     * @param: touchHandler = interface for communicating touched events' information to activity
     */
    inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {

        val layout: ScrollView = itemView.findViewById(R.id.directory_recycler_layout_topic)
        val image: ImageView = itemView.findViewById(R.id.directory_recycler_topic_image)
        val description: TextView = itemView.findViewById(R.id.directory_recycler_topic_description)

        private val editLayout: LinearLayout = itemView.findViewById(R.id.directory_recycler_layout_edit)
        private val editButtonImage: Button = itemView.findViewById(R.id.directory_recycler_button_edit_image)
        private val editButtonDesc: Button = itemView.findViewById(R.id.directory_recycler_button_edit_description)
        private val editButtonDelete: Button = itemView.findViewById(R.id.directory_recycler_button_edit_delete)

        init {
            layout.setOnTouchListener(this)

            editButtonImage.setOnTouchListener(this)
            editButtonDesc.setOnTouchListener(this)
            editButtonDelete.setOnTouchListener(this)
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {

            if(event == null)
                return false

            when(view?.id) {
                R.id.directory_recycler_layout_topic -> {
                    Log.i("Touch Event", "DirectoryRecyclerAdapter#onTouch(): touched topic layout!")

                    if(selectedTopic.isEditVisible() && (event.action == MotionEvent.ACTION_DOWN)) {
                        selectedTopic.setUnselected()

                        if(itemId == selectedTopic.getId())
                            return true
                    }

                    selectedTopic.assignValues(itemId, editLayout)

                    touchHandler.topicTouch(itemId, event)
                }

                R.id.directory_recycler_button_edit_image -> {
                    touchHandler.topicEditImage(itemId, event)
                }

                R.id.directory_recycler_button_edit_description -> {
                    touchHandler.topicEditDesc(itemId, event)
                }

                R.id.directory_recycler_button_edit_delete -> {
                    touchHandler.topicDelete(itemId, event)
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

    class SelectedTopic(
        private var id: Long = -1L,
        private var editLayout: LinearLayout? = null) {

        fun getId() = id

        fun assignValues(id: Long, editLayout: LinearLayout) {
            this.id = id
            this.editLayout = editLayout
        }

        fun setSelected() {
            editLayout?.visibility = View.VISIBLE
        }

        fun setUnselected() {
            editLayout?.visibility = View.GONE
        }

        fun isEditVisible() = editLayout?.visibility == View.VISIBLE
    }

    interface DirectoryAdapterHandler {

        fun topicTouch(id: Long, event: MotionEvent)

        fun topicEditImage(id: Long, event: MotionEvent)

        fun topicEditDesc(id: Long, event: MotionEvent)

        fun topicDelete(id: Long, event: MotionEvent)
    }
}