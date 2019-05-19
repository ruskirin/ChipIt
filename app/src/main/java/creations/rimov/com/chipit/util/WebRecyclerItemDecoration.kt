package creations.rimov.com.chipit.util

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import creations.rimov.com.chipit.R

class WebRecyclerItemDecoration(private var position: Int) : RecyclerView.ItemDecoration() {

    fun setSelectedItem(position: Int) {
        this.position = position
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val itemPos = parent.getChildAdapterPosition(view)
        val highlight = ContextCompat.getColor(view.context, R.color.colorRecyclerViewHighlight)

        if(itemPos == position) {
            view.setBackgroundColor(highlight)
        }
    }
}