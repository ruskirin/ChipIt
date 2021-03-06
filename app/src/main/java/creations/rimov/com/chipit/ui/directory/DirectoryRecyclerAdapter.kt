package creations.rimov.com.chipit.ui.directory

import android.view.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.data.objects.ChipCard
import creations.rimov.com.chipit.data.objects.ChipIdentity
import creations.rimov.com.chipit.ui.editor.CardLayout
import creations.rimov.com.chipit.ui.TopicLayout
import kotlinx.android.synthetic.main.recycler_directory_chip.view.*


//TODO: add Glide Recyclerview integration if scrolling causes stuttering

//TODO (IMPORTANT): might be a memory leak with TextView here, check logs for the message, then follow:
//                   https://stackoverflow.com/questions/49513726/android-memory-leak-with-fragments


class DirectoryRecyclerAdapter(private val touchHandler: DirectoryAdapterHandler)
    : RecyclerView.Adapter<DirectoryRecyclerAdapter.DirectoryViewHolder>() {

    private lateinit var topics: List<ChipIdentity>
    //Reference to selected ViewHolder
    lateinit var selectedTopic: DirectoryViewHolder

    init {
        //Adapter does not return proper id from overriden #getItemId() otherwise
        setHasStableIds(true)
    }

    fun setTopics(topics: List<ChipIdentity>) {
        this.topics = topics

        notifyDataSetChanged()
    }

    fun setChildren(children: List<ChipCard>) {

        if(::selectedTopic.isInitialized) selectedTopic.setChildren(children)
        else return
    }

    fun getSelectedId() =
        if(::selectedTopic.isInitialized) selectedTopic.itemId
        else -1L


    inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {

        val topicChip: TopicLayout = itemView.dirRecyclerTopic
        val editLayout: CardLayout = itemView.dirRecyclerTopicEdit

        init {
            topicChip.setTouchListener(this)
            editLayout.setTouchListener(this)
        }

        private fun setSelectedTopic(topic: DirectoryViewHolder) {

            if(!::selectedTopic.isInitialized)
                selectedTopic = topic

            if(selectedTopic.itemId == itemId)
                return

            selectedTopic.edit(false)

            selectedTopic = topic
        }

        fun setChildren(children: List<ChipCard>) {
            topicChip.setChildren(children)
        }

        fun isEditing() = editLayout.isVisible

        fun isExpanded() = topicChip.isExpanded

        fun edit(edit: Boolean) {

            if(edit) editLayout.show(false)
            else editLayout.hide()
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {

            if(event == null) return false

            when(view?.id) {

                R.id.topicLayoutHeader -> {
                    if(event.action == MotionEvent.ACTION_DOWN)
                        setSelectedTopic(this)

                    touchHandler.topicTouch(event)
                }
                R.id.topicLayoutBtnCount -> {
                    if(event.action == MotionEvent.ACTION_DOWN)
                        setSelectedTopic(this)

                    if(event.action == MotionEvent.ACTION_UP)
                        touchHandler.topicToWeb()
                }
                R.id.cardEditorBtnEdit -> {
                    if(event.action == MotionEvent.ACTION_UP)
                        touchHandler.topicEdit(topics[adapterPosition].id)
                }
                R.id.cardEditorBtnDelete -> {
                    if(event.action == MotionEvent.ACTION_UP)
                        touchHandler.topicDelete(topics[adapterPosition].id)
                }
            }

            view?.performClick()
            return true
        }
    }

    override fun getItemCount() =
        if(::topics.isInitialized) topics.size
        else 0

    override fun getItemId(position: Int) = topics[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {

        val chipHolder =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_directory_chip, parent, false)

        return DirectoryViewHolder(chipHolder)
    }

    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {

        holder.topicChip.setTopic(topics[position])
    }

    interface DirectoryAdapterHandler {

        fun topicTouch(event: MotionEvent)

        fun topicToWeb()

        fun topicEdit(chipId: Long)

        fun topicDelete(chipId: Long)
    }
}