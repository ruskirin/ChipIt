package creations.rimov.com.chipit.adapters

import android.view.MotionEvent
import creations.rimov.com.chipit.adapters.viewholders.web.WebViewHolder

interface ViewHolderHandler {

    fun handleGesture(event: MotionEvent?, holder: WebViewHolder? = null)
}