package creations.rimov.com.chipit.recyclers.adapters

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.recyclers.adapters.viewholders.web.*
import creations.rimov.com.chipit.constants.EditorConsts
import creations.rimov.com.chipit.database.objects.ChipCard

//TODO FUTURE: images can be linked through either a file path or as bitmap,
//  both have pros and cons

class WebRecyclerAdapter(appContext: Context)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
      ViewHolderHandler {

    private lateinit var chips: List<ChipCard>
    //Reference to the touched chip
    private lateinit var selectedChip: WebViewHolder

    var touchI: Pair<Float, Float> = Pair(0f, 0f)

    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(appContext, WebGestureDetector())
    }

    init {
        //Adapter does not return proper id from overriden #getItemId() otherwise
        setHasStableIds(true)
        gestureDetector.setIsLongpressEnabled(true)
    }

    fun setChips(chips: List<ChipCard>) {
        this.chips = chips

        notifyDataSetChanged()
    }

    fun setSelected(holder: WebViewHolder) {

        if(::selectedChip.isInitialized
           && selectedChip==holder) return

        selectedChip = holder
    }

    fun getSelectedChip() =
        if(::selectedChip.isInitialized) selectedChip
        else null

    fun isEditing(): Boolean = getSelectedChip()?.isEditing ?: true

    override fun getItemCount() =
        if (::chips.isInitialized) chips.size
        else 0

    override fun getItemId(position: Int) = chips[position].id

    override fun getItemViewType(position: Int): Int = chips[position].matType

    override fun onCreateViewHolder(
      parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType) {
            EditorConsts.IMAGE -> {
                LayoutInflater.from(parent.context).inflate(
                  R.layout.recycler_web_card_image,
                  parent,
                  false).let {
                    WebViewHolderImage(it)
                }
            }
            EditorConsts.VIDEO -> {
                LayoutInflater.from(parent.context).inflate(
                  R.layout.recycler_web_card_video,
                  parent,
                  false).let {
                    WebViewHolderVideo(it)
                }
            }
            EditorConsts.AUDIO -> {
                LayoutInflater.from(parent.context).inflate(
                  R.layout.recycler_web_card_audio,
                  parent,
                  false).let {
                    WebViewHolderAudio(it)
                }
            }
            EditorConsts.TEXT -> {
                LayoutInflater.from(parent.context).inflate(
                  R.layout.recycler_web_card_image,
                  parent,
                  false).let {
                    WebViewHolderText(
                      it)
                }
            }
            else -> {
                LayoutInflater.from(parent.context).inflate(
                  R.layout.recycler_web_card_image,
                  parent,
                  false).let {
                    WebViewHolderImage(
                      it)
                }
            }
        }
    }

    override fun onBindViewHolder(
      holder: RecyclerView.ViewHolder, position: Int) {

        when(holder) {
            is WebViewHolder -> {
                holder.prepare(this, chips[position])
            }
        }
    }

    override fun handleGesture(event: MotionEvent?, holder: WebViewHolder?) {

        holder?.let {
            //Holder is only passed on ACTION_DOWN when a chip is selected
            if(event?.action==MotionEvent.ACTION_DOWN) setSelected(it)
        }

        gestureDetector.onTouchEvent(event)
    }


    inner class WebGestureDetector : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {

            e?.let {touchI = Pair(it.rawX, it.rawY)}
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            super.onLongPress(e)
        }
    }
}