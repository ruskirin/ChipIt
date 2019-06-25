package creations.rimov.com.chipit.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object CameraUtil {

    class ImageFile(val file: File, val storagePath: String)

    const val CODE_TAKE_PICTURE = 1
    const val IMAGE_PROVIDER_AUTHORITY = "com.rimov.creations.chipit.imageprovider"

    @JvmStatic
    fun getImageFile(context: Context,
                     fileName: String = "chipit_chip_",
                     storageDir: File? = null): ImageFile? {
        //TODO: handle error
        return try {
            createImageFile(
                context,
                fileName,
                storageDir ?: context.getExternalFilesDir(Environment.DIRECTORY_PICTURES))

        } catch(e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    fun getImageUri(context: Context, imageFile: File): Uri {

        return FileProvider.getUriForFile(context, IMAGE_PROVIDER_AUTHORITY, imageFile)
    }

    //TODO: consider adding a verification method to ensure no unnecessary files are somehow deleted
    @JvmStatic
    fun deleteImageFile(imagePath: String) = File(imagePath).delete()

    //TODO: handle IOException
    @JvmStatic
    private fun createImageFile(context: Context, fileName: String, storageDir: File): ImageFile {
        //TODO: consider modifying locale based on phone location
        val time = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
        val file = File.createTempFile("$fileName$time", ".jpg", storageDir)

        Log.i("ImageFile", "File path: ${file.absoluteFile}")

        return ImageFile(file, file.absolutePath)
    }
}