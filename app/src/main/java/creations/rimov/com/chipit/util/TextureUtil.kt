package creations.rimov.com.chipit.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log

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

    class AsyncPathToBitmap(private val handler: AsyncHandler,
                            private val sampleSize: Int) : AsyncTask<String, Void, Bitmap?>() {

        override fun doInBackground(vararg params: String?): Bitmap? {

            BitmapFactory.Options().apply {
                inSampleSize = sampleSize

                return params[0]?.let { path ->
                    BitmapFactory.decodeFile(path, this)
                }
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            result?.let {handler.setData(it)}
        }
    }
}