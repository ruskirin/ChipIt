package creations.rimov.com.chipit.util

import android.content.Context
import android.content.Intent
import android.net.Uri
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

    class ImageFile(val file: File, val storagePath: String)

    const val CODE_TAKE_PICTURE = 1
    const val IMAGE_PROVIDER_AUTHORITY = "com.rimov.creations.chipit.fileprovider"

    private const val IMG_FILENAME_PREFIX = "ChipIt_IMG_"

    @JvmStatic
    fun getImageFile(storageDir: File? = null): ImageFile? {

        //TODO FUTURE: handle this
        if(!isExternalStorageAvailable()) return null

        return try {
            createImageFile(storageDir ?: Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))

        } catch(e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    fun getImageUri(context: Context, imageFile: File): Uri =
        FileProvider.getUriForFile(context, IMAGE_PROVIDER_AUTHORITY, imageFile)

    //TODO: consider adding a verification method to ensure no unnecessary files are somehow deleted
    @JvmStatic
    fun deleteImageFile(imagePath: String) = File(imagePath).delete()

    @JvmStatic
    private fun createImageFile(storageDir: File): ImageFile {
        //TODO: consider modifying locale based on phone location
        val time = Date().getChipFileDate()
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "$IMG_FILENAME_PREFIX$time.jpg")

        Log.i("ImageFile", "File path: ${file.absoluteFile}")

        return ImageFile(file, file.absolutePath)
    }

    @JvmStatic
    private fun isExternalStorageAvailable(): Boolean = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}