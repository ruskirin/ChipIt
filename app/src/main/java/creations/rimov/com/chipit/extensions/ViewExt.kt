package creations.rimov.com.chipit.extensions

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