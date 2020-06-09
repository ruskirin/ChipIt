package creations.rimov.com.chipit.adapters.viewholders.web

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class WebTouchCallback(private val handler: Handler)
    : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
      recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int =
        makeMovementFlags(
          0,
          ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

    //Not used
    override fun onMove(
      recyclerView: RecyclerView,
      viewHolder: RecyclerView.ViewHolder,
      target: RecyclerView.ViewHolder): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        when(direction) {
            ItemTouchHelper.LEFT -> {
                Log.i("WebTouchCallback", "::onSwiped(): swiped left")
                (viewHolder as WebViewHolder).toggleEdit(true)
            }
            ItemTouchHelper.RIGHT -> {
                Log.i("WebTouchCallback", "::onSwiped(): swiped right")
                handler.promptDelete(viewHolder.itemId)
            }
        }
    }

    override fun convertToAbsoluteDirection(
      flags: Int, layoutDirection: Int): Int {


        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    interface Handler {

        fun promptDelete(id: Long)
    }
}