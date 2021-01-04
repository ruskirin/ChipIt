package creations.rimov.com.chipit.ui.web.adapters

import android.view.MotionEvent
import creations.rimov.com.chipit.ui.web.adapters.viewholders.web.WebViewHolder

interface ViewHolderHandler {

    fun handleGesture(event: MotionEvent?, holder: WebViewHolder? = null)
}