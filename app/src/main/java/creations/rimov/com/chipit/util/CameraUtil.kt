package creations.rimov.com.chipit.util

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

    class ImageFile(val file: File, val storagePath: String)

    private const val IMAGE_PROVIDER_AUTHORITY = "com.rimov.creations.chipit.fileprovider"
    private const val IMG_FILENAME_PREFIX = "ChipIt_IMG_"

    const val CODE_TAKE_PICTURE = 100
    const val CODE_GET_IMAGE = 200

    //Used to create an ImageFile for devices SDK <29
    @JvmStatic
    fun getImageFileOld(storageDir: File? = null): ImageFile? {

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            return getImageFileNew(storageDir)
        }

        //TODO FUTURE: handle this
        if(!isExternalStorageAvailable()) {
            Log.e("Image Creation", "CameraUtil#getImageFileOld(): external storage not available!")
            return null
        }

        return try {
            //Starting SDK 29, getExternalStoragePublicDirectory is deprecated and other methods need to be used
            val directory = storageDir ?: Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            createImageFile(directory)

        } catch(e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    fun getImageFileNew(storageDir: File? = null): ImageFile? {

        TODO("Follow the bookmarked method using MediaStore")

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            return getImageFileOld(storageDir)
        }

        //TODO FUTURE: handle this
        if(!isExternalStorageAvailable()) {
            Log.e("Image Creation", "CameraUtil#getImageFileNew(): external storage not available!")
            return null
        }

        return try {
            //Starting SDK 29, getExternalStoragePublicDirectory is deprecated and other methods need to be used
            val directory = storageDir ?:
                            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            else null //TODO FUTURE: find alternative for SDK 29+

            createImageFile(directory)

        } catch(e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    fun getImageUri(context: Context, imageFile: File): Uri =
        FileProvider.getUriForFile(context, IMAGE_PROVIDER_AUTHORITY, imageFile)

    //TODO: consider adding a verification method to ensure no unintended files are somehow deleted
    @JvmStatic
    fun deleteImageFile(imagePath: String) = File(imagePath).delete()

    //Create an ImageFile containing a File and string indicating the path of the image
    @JvmStatic
    private fun createImageFile(storageDir: File?): ImageFile? {

        if(storageDir == null) return null

        //TODO: consider modifying locale based on phone location
        val time = Date().getChipFileDate() //Part of the file name
        val file = File(storageDir, "$IMG_FILENAME_PREFIX$time.jpg")

        Log.i("ImageFile", "CameraUtil#createImageFile(): Image at ${file.absolutePath}")

        return ImageFile(file, file.absolutePath)
    }

    @JvmStatic
    private fun isExternalStorageAvailable(): Boolean = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}