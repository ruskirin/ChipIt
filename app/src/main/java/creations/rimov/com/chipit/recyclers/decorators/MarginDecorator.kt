package creations.rimov.com.chipit.recyclers.decorators

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class MarginDecorator(private val space: Int)
    : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
      outRect: Rect,
      view: View,
      parent: RecyclerView,
      state: RecyclerView.State) {

        val index = (view.layoutParams as StaggeredGridLayoutManager.LayoutParams)
            .spanIndex

        outRect.apply {
            bottom = space
            right = space

            //Ensure no double margins between rows/columns
            if(parent.getChildAdapterPosition(view) == 0
               || parent.getChildAdapterPosition(view) == 1)
                top = space
            if(index == 0)
                left = space
        }
    }
}