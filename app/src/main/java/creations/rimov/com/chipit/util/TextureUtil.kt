package creations.rimov.com.chipit.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory

object TextureUtil {

    /**
     * Instead of returning the bitmap, #inJustDecodeBounds returns the properties of the passed image
     * Tutorial for reference: https://stuff.mit.edu/afs/sipb/project/android/docs/training/displaying-bitmaps/load-bitmap.html
     */
    fun getImageFileDimen(path: String): Array<Int> {

        //TODO FUTURE: also returns MIME type, see if useful
        BitmapFactory.Options().apply {
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(path, this)

            return arrayOf(this.outWidth, this.outHeight)
        }
    }

    fun convertPathToBitmap(path: String, sampleSize: Int): Bitmap? {

        TODO("Still takes up frames, so do this in an AsyncTask")

        BitmapFactory.Options().apply {
            inSampleSize = sampleSize

            return BitmapFactory.decodeFile(path, this)
        }
    }
}