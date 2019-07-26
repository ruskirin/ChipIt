package creations.rimov.com.chipit.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import java.io.InputStream

object TextureUtil {

    /**
     * Instead of returning the bitmap, #inJustDecodeBounds returns the properties of the passed image
     * Tutorial for reference: https://stuff.mit.edu/afs/sipb/project/android/docs/training/displaying-bitmaps/load-bitmap.html
     */
//    fun getBitmapDimen(stream: InputStream): Array<Int> {
//
//        //TODO FUTURE: also returns MIME type, see if useful
//        BitmapFactory.Options().apply {
//            inJustDecodeBounds = true
//
//            BitmapFactory.decodeStream(stream, null, this)
//
//            return arrayOf(this.outWidth, this.outHeight)
//        }
//    }

    fun getBitmapDimen(context: Context?, uri: String?): Array<Int>? {

        Log.i("Life Event", "TextureUtil#getBitmapDimen(): received uri: $uri")

        val stream = context?.contentResolver?.openInputStream(
              Uri.parse(uri)) ?: return null

        //TODO FUTURE: also returns MIME type, see if useful
        BitmapFactory.Options().apply {
            inJustDecodeBounds = true

            BitmapFactory.decodeStream(stream, null, this)

            stream.close()

            return arrayOf(this.outWidth, this.outHeight)
        }
    }

    class AsyncPathToBitmap(private val handler: AsyncHandler,
                            private val sampleSize: Int) : AsyncTask<InputStream, Void, Bitmap?>() {

        override fun doInBackground(vararg params: InputStream): Bitmap? {

            BitmapFactory.Options().apply {
                inSampleSize = sampleSize

                val bitmap = BitmapFactory.decodeStream(params[0], null, this)

                params[0].close()

                return bitmap
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            result?.let {handler.setData(it)}
        }
    }
}