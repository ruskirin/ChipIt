package creations.rimov.com.chipit.extension

import android.view.View

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