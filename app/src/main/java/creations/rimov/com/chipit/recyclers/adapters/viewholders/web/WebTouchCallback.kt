package creations.rimov.com.chipit.recyclers.adapters.viewholders.web

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class WebTouchCallback
    : ItemTouchHelper.Callback() {

    //Flag to monitor when an item gets swiped off screen so as to return it
    var revertSwiped = false

    override fun getMovementFlags(
      recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int =
        makeMovementFlags(
          0,
          ItemTouchHelper.UP)

    //Not used
    override fun onMove(
      recyclerView: RecyclerView,
      viewHolder: RecyclerView.ViewHolder,
      target: RecyclerView.ViewHolder): Boolean = false

    //Not used due to use of revertSwiped
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun convertToAbsoluteDirection(
      flags: Int, layoutDirection: Int): Int {

        if(revertSwiped) {
            revertSwiped = false //Reset flag
            return 0 //Return to starting position

        } else
            return super.convertToAbsoluteDirection(flags, layoutDirection)
    }
}