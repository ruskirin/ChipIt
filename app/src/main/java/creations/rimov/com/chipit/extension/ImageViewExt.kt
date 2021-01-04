package creations.rimov.com.chipit.extension

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun <T> ImageView.displayImage(imgLocation: T?) {

    imgLocation?.let {
        Glide.with(this.context)
            .load(it)
            .apply(
              RequestOptions()
                  .override(this.width, this.height))
            .into(this)

        return
    }

    Glide.with(this).clear(this) //Clears any resources in image
}