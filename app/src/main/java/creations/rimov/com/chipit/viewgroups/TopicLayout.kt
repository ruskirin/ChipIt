package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.adapters.TopicChipAdapter
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipTopic
import kotlinx.android.synthetic.main.topic_layout.view.*

class TopicLayout(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val title: TextView by lazy {topicLayoutTitle}
    private val counter: TextView by lazy {topicLayoutChildCount}
    private val dateCreate: TextView by lazy {topicLayoutDateCreate}
    private val dateUpdate: TextView by lazy {topicLayoutDateUpdate}
    private val description: TextView by lazy {topicLayoutDesc}

    private val chipRecycler: RecyclerView by lazy {topicLayoutChildren}
    private val chipAdapter: TopicChipAdapter by lazy {TopicChipAdapter(context)}

    init {
        View.inflate(context, R.layout.topic_layout, this)

        chipRecycler.apply {
            adapter = chipAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        topicLayoutBtnExpand.setOnClickListener { button ->

            if(chipRecycler.isVisible) {
                hideChildren()

            } else {

            }
        }
    }

    fun setTopic(topic: ChipTopic) {

        title.text = topic.name
        counter.text = topic.counter.toString()
        dateCreate.text = topic.dateCreate
        dateUpdate.text = topic.dateUpdate
        description.text = topic.desc
    }

    fun setChildren(chips: List<ChipCard>) {
        chipAdapter.setChildren(chips)
        chipAdapter.notifyDataSetChanged()
    }

    fun setClickListener(listener: OnClickListener) {
        topicLayoutBtnExpand.setOnClickListener(listener)
    }

    fun showChildren() {
        chipRecycler.visibility = View.VISIBLE
    }

    fun hideChildren() {
        chipRecycler.visibility = View.GONE
    }
}