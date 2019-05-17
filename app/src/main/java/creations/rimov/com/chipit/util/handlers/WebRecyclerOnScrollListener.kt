package creations.rimov.com.chipit.util.handlers

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import creations.rimov.com.chipit.extensions.getSnapPos

class WebRecyclerOnScrollListener(
    private val snapHelper: SnapHelper,
    private val snapChangeListener: OnSnapListener) : RecyclerView.OnScrollListener() {


    private var position = RecyclerView.NO_POSITION

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

        if(newState == RecyclerView.SCROLL_STATE_IDLE)
            notifySnapPosChange(recyclerView)
    }

    private fun notifySnapPosChange(recyclerView: RecyclerView) {
        val snapPosition = snapHelper.getSnapPos(recyclerView)

        if(snapPosition != position) {
            snapChangeListener.onSnapPosChange(snapPosition)
            position = snapPosition
        }
    }
}