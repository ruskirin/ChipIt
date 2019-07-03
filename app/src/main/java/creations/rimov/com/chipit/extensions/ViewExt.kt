package creations.rimov.com.chipit.extensions

import android.view.View

fun View.gone() {
    if(this.visibility == View.VISIBLE || this.visibility == View.INVISIBLE) this.visibility = View.GONE
}

fun View.visible() {
    if(this.visibility == View.GONE || this.visibility == View.INVISIBLE) this.visibility = View.VISIBLE
}