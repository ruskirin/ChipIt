package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.adapters.TopicChipAdapter
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import kotlinx.android.synthetic.main.topic.view.*

class TopicLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val count: TextView by lazy {topicLayoutChildCount}
    private val btnCount: ImageButton by lazy {topicLayoutBtnCount}

    private val desc: TextView by lazy {topicLayoutDesc}
    private val chipAdapter: TopicChipAdapter by lazy {TopicChipAdapter()}

    var isExpanded = false

    init {
        View.inflate(context, R.layout.topic, this)
    }

    fun setTopic(topic: ChipIdentity) {

        topicLayoutTitle.text = topic.name
        count.text = topic.numChildren.toString()
        topicLayoutDateUpdate.text = topic.date
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

    //TODO URGENT: problem with visibility toggling of topics that have been used
    // to navigate to Web, have to first toggle another topic to get it working
    fun toggleDetail() {

        if(desc.visibility == View.VISIBLE) {
            showCountBtn(false)
            desc.visibility = View.GONE
            isExpanded = false

        } else {
            showCountBtn(true)
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