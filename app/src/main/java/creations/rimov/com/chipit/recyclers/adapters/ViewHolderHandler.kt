package creations.rimov.com.chipit.recyclers.adapters

import android.view.MotionEvent
import creations.rimov.com.chipit.recyclers.adapters.viewholders.web.WebViewHolder

interface ViewHolderHandler {

    fun handleGesture(event: MotionEvent?, holder: WebViewHolder? = null)
}