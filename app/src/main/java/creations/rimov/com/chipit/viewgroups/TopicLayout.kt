package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
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

class TopicLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val count: TextView by lazy {topicLayoutChildCount}
    private val btnCount: ImageButton by lazy {topicLayoutBtnCount}

    private val desc: TextView by lazy {topicLayoutDesc}
    private val chipRecycler: RecyclerView by lazy {topicLayoutChildren}
    private val chipAdapter: TopicChipAdapter by lazy {TopicChipAdapter()}

    var isExpanded = false

    init {
        View.inflate(context, R.layout.topic_layout, this)

        chipRecycler.apply {
            adapter = chipAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
        }
    }

    fun setTopic(topic: ChipTopic) {

        topicLayoutTitle.text = topic.name
        count.text = topic.counter.toString()
        topicLayoutDateCreate.text = topic.dateCreate
        topicLayoutDateUpdate.text = topic.dateUpdate
        desc.text = topic.desc
    }

    fun setChildren(chips: List<ChipCard>) {

        chipAdapter.setChildren(chips)
        chipAdapter.notifyDataSetChanged()
    }

    fun setTouchListener(listener: OnTouchListener) {

        topicLayoutHeader.setOnTouchListener(listener)
        btnCount.setOnTouchListener(listener)
    }

    fun toggleDetail() {

        if(desc.visibility == View.VISIBLE) {
            showCountBtn(false)

            chipRecycler.visibility = View.GONE
            desc.visibility = View.GONE

            isExpanded = false

        } else {
            showCountBtn(true)

            chipRecycler.visibility = View.VISIBLE
            desc.visibility = View.VISIBLE

            isExpanded = true
        }
    }

    private fun showCountBtn(show: Boolean) {

        if(show) {
            count.visibility = View.GONE
            btnCount.visibility = View.VISIBLE

        } else {
            count.visibility = View.VISIBLE
            btnCount.visibility = View.GONE
        }
    }
}