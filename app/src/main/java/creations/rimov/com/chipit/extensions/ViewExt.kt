package creations.rimov.com.chipit.extensions

import android.util.Log
import android.view.View
import android.widget.ImageView

fun View?.gone() {
    this?.let {it.visibility = View.GONE}
}

fun View?.visible() {
    this?.let {it.visibility = View.VISIBLE}
}

fun View?.invisible() {
    this?.let {it.visibility = View.INVISIBLE}
}

fun View?.setDimen(width: Int? = null, height: Int? = null) {

    this?.let {
        val params = this.layoutParams

        width?.let {params.width = width}
        height?.let {params.height = height}

        layoutParams = params
    }
}