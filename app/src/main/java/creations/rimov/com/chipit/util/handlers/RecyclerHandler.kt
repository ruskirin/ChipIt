package creations.rimov.com.chipit.util.handlers

import android.view.MotionEvent

interface RecyclerHandler {

    fun topicTouch(position: Int, event: MotionEvent, list: Int = -1)
}