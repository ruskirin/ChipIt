package creations.rimov.com.chipit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipCard

//TODO (FUTURE): images can be linked through either a file path or as bitmap, both have pros and cons

class AlbumRecyclerAdapter(private val context: Context,
                           private val touchHandler: AlbumAdapterHandler)
    : RecyclerView.Adapter<AlbumRecyclerAdapter.ChipViewHolder>() {

    private lateinit var chips: List<ChipCard>
    //Reference to the touched chip
    private lateinit var selectedChip: ChipViewHolder

    init {
        //Adapter does not return proper id from overriden #getItemId() otherwise
        setHasStableIds(true)
    }

    fun setChips(chips: List<ChipCard>) {
        this.chips = chips

        notifyDataSetChanged()
    }

    fun isEditing() = selectedChip.isEditing()

    fun toggleEdit() {

        if(::selectedChip.isInitialized)
            selectedChip.toggleEdit()
    }

    /**
     * VIEW HOLDER
     */
    inner class ChipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {

        val image: ImageView = itemView.findViewById(R.id.album_recycler_chip_image)
        val topic: TextView = itemView.findViewById(R.id.album_recycler_chip_topic)
        val buttonExpand: ImageButton = itemView.findViewById(R.id.album_recycler_chip_button_expand)

        private val editLayout: LinearLayout = itemView.findViewById(R.id.album_recycler_edit_button_layout)
        private val editButtonImage: Button = itemView.findViewById(R.id.album_recycler_edit_button_image)
        private val editButtonTopic: Button = itemView.findViewById(R.id.album_recycler_edit_button_topic)
        private val editButtonDelete: Button = itemView.findViewById(R.id.album_recycler_edit_button_delete)

        init {
            image.setOnTouchListener(this)

            buttonExpand.setOnTouchListener(this)
            editButtonImage.setOnTouchListener(this)
            editButtonTopic.setOnTouchListener(this)
            editButtonDelete.setOnTouchListener(this)
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {

            if(event == null)
                return false

            val id = itemId

            if(event.action == MotionEvent.ACTION_UP) {

                when(view?.id) {

                    R.id.album_recycler_chip_button_expand -> {
                        touchHandler.topicExpand(id)

                        return true
                    }

                    R.id.album_recycler_edit_button_image -> {


                        return true
                    }

                    R.id.album_recycler_edit_button_topic -> {


                        return true
                    }

                    R.id.album_recycler_edit_button_delete -> {
                        touchHandler.topicDelete(id)

                        return true
                    }
                }
            }
            //Initialize
            if(!::selectedChip.isInitialized)
                selectedChip = this

            //Toggle editing of previous selected chip and reassign to the one worked on now
            if(id != selectedChip.itemId) {

                if(selectedChip.isEditing())
                    selectedChip.toggleEdit()

                selectedChip = this
            }

            touchHandler.topicTouch(itemId, event)

            view?.performClick()

            return true
        }

        fun isEditing() = selectedChip.editLayout.isVisible

        fun toggleEdit() {
             if(selectedChip.isEditing())
                 selectedChip.editLayout.visibility = View.GONE
             else
                 selectedChip.editLayout.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() =
        if(::chips.isInitialized)
            chips.size
        else
            0

    override fun getItemId(position: Int) = chips[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {

        val chipHolder = LayoutInflater.from(context)
            .inflate(R.layout.album_recycler_chip_layout, parent, false)

        return ChipViewHolder(chipHolder)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {

        holder.topic.text = chips[position].description
        //TODO: load a default image if none can be found
        Glide.with(context)
            .load(chips[position].imgLocation)
            .into(holder.image)
    }

    interface AlbumAdapterHandler {

        fun topicTouch(id: Long, event: MotionEvent)

        fun topicExpand(id: Long)

        fun topicDelete(id: Long)
    }
}