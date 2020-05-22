package creations.rimov.com.chipit.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import creations.rimov.com.chipit.extensions.getChipFileDate
import creations.rimov.com.chipit.extensions.getChipUpdateDate
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object CameraUtil {

    private const val IMAGE_PROVIDER_AUTHORITY = "com.rimov.creations.chipit.fileprovider"
    private const val IMG_FILENAME_PREFIX = "ChipIt_IMG_"

    const val CODE_TAKE_PICTURE = 100
    const val CODE_GET_IMAGE = 200

    //
    @JvmStatic
    fun getImageFile(storageDir: File? = null): File? {

        //TODO FUTURE: handle this
        if(!isExternalStorageAvailable()) {
            Log.e("Image Creation",
                  "CameraUtil#getImageFile(): external storage not available!")
            return null
        }

        return try {
            //Starting SDK 29, getExternalStoragePublicDirectory is deprecated
            //  and other methods need to be used
            val directory = storageDir
                            ?: Environment
                                .getExternalStoragePublicDirectory(
                                  Environment.DIRECTORY_PICTURES)

            createImageFile(directory)

        } catch(e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    fun getImageUri(context: Context, imageFile: File): Uri =
        FileProvider.getUriForFile(context, IMAGE_PROVIDER_AUTHORITY, imageFile)

    @JvmStatic
    fun getImageUriNew(context: Context): Uri? {

        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, IMG_FILENAME_PREFIX + Date().getChipFileDate())
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/ChipIt")
        }

        return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    //TODO: consider adding a verification method to ensure no unintended files are somehow deleted
    @JvmStatic
    fun deleteImageFile(imagePath: String) = File(imagePath).delete()

    @JvmStatic
    private fun createImageFile(storageDir: File): File {

        //TODO: consider modifying locale based on phone location
        val time = Date().getChipFileDate() //Part of the file name
        val file = File(storageDir, "$IMG_FILENAME_PREFIX$time.jpg")

        return file
    }

    @JvmStatic
    private fun isExternalStorageAvailable(): Boolean = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}