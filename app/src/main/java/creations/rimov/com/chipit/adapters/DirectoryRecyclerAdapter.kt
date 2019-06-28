package creations.rimov.com.chipit.adapters

import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipTopic
import creations.rimov.com.chipit.viewgroups.CardEditorLayout
import creations.rimov.com.chipit.viewgroups.TopicLayout
import kotlinx.android.synthetic.main.directory_recycler_chip_layout.view.*


//TODO: add Glide Recyclerview integration if scrolling causes stuttering

//TODO (IMPORTANT): might be a memory leak with TextView here, check logs for the message, then follow:
//                   https://stackoverflow.com/questions/49513726/android-memory-leak-with-fragments


class DirectoryRecyclerAdapter(private val touchHandler: DirectoryAdapterHandler)
    : RecyclerView.Adapter<DirectoryRecyclerAdapter.DirectoryViewHolder>() {

    private lateinit var topics: List<ChipTopic>
    //Reference to selected ViewHolder
    lateinit var selectedTopic: DirectoryViewHolder

    init {
        //Adapter does not return proper id from overriden #getItemId() otherwise
        setHasStableIds(true)
    }

    fun setTopics(topics: List<ChipTopic>) {
        this.topics = topics

        notifyDataSetChanged()
    }

    fun setChildren(children: List<ChipCard>) {

        if(::selectedTopic.isInitialized)
            selectedTopic.setChildren(children)
        else return
    }

    fun getSelectedId() =
        if(::selectedTopic.isInitialized)
            selectedTopic.itemId
        else -1L


    inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {

        val topicChip: TopicLayout = itemView.dirRecyclerTopic
        val editLayout: CardEditorLayout = itemView.dirRecyclerTopicEdit

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

            if(event == null)
                return false

            when(view?.id) {

                R.id.topicLayoutHeader -> {

                    Log.i("Touch Event", "DirectoryViewHolder#onTouch(): RecyclerTopic touched!")

                    if(event.action == MotionEvent.ACTION_DOWN)
                        setSelectedTopic(this)

                    touchHandler.topicTouch(event)
                }

                R.id.topicLayoutBtnCount -> {

                    Log.i("Touch Event", "DirectoryViewHolder#onTouch(): BtnCount touched!")

                    if(event.action == MotionEvent.ACTION_DOWN)
                        setSelectedTopic(this)

                    if(event.action == MotionEvent.ACTION_UP)
                        touchHandler.topicToWeb()
                }

                R.id.cardEditorBtnEdit -> {

                    if(event.action == MotionEvent.ACTION_UP)
                        touchHandler.topicEdit(topics[adapterPosition])
                }

                R.id.cardEditorBtnDelete -> {

                    if(event.action == MotionEvent.ACTION_UP)
                        touchHandler.topicDelete(topics[adapterPosition])
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
            LayoutInflater.from(parent.context).inflate(R.layout.directory_recycler_chip_layout, parent, false)

        return DirectoryViewHolder(chipHolder)
    }

    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {

        holder.topicChip.setTopic(topics[position])
    }

    interface DirectoryAdapterHandler {

        fun topicTouch(event: MotionEvent)

        fun topicToWeb()

        fun topicEdit(chip: ChipTopic)

        fun topicDelete(chip: ChipTopic)
    }
}