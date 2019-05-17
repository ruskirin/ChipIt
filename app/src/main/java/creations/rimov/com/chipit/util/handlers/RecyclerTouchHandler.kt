package creations.rimov.com.chipit.util.handlers

import android.view.MotionEvent

interface RecyclerTouchHandler {

    fun topicTouch(position: Int, event: MotionEvent, listType: Int = -1)
}