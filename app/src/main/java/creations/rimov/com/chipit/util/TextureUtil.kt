package creations.rimov.com.chipit.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory

object TextureUtil {

    fun convertPathToBitmap(path: String): Bitmap? = BitmapFactory.decodeFile(path)
}