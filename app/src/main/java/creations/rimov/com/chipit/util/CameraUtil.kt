package creations.rimov.com.chipit.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import creations.rimov.com.chipit.util.constants.EditorConsts
import creations.rimov.com.chipit.extension.getChipFileDate
import java.io.File
import java.io.IOException
import java.util.*

object CameraUtil {

    private const val IMAGE_PROVIDER_AUTHORITY =
        "com.rimov.creations.chipit.fileprovider"

    private const val IMG_PREFIX = "ChipIt_IMG_"
    private const val VIDEO_PREFIX = "ChipIt_VID_"
    private const val AUDIO_PREFIX = "Chipit_AUD_"

    const val CAPTURE_PIC = 100
    const val FIND_PIC = 110

    const val CAPTURE_VID = 200
    const val FIND_VID = 210
    const val FIND_AUDIO = 220

    @JvmStatic
    fun intentCaptureMedia(appContext: Context, uri: Uri?, type: Int): Intent? {

        val intent = if(type==EditorConsts.IMAGE)
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                     else if(type==EditorConsts.VIDEO)
            Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                     else if(type==EditorConsts.AUDIO)
            Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION )
                     else null

        return intent?.apply {
            //Verifies that an application that can handle this intent exists
            if(this.resolveActivity(appContext.packageManager)==null)
                throw UnsupportedOperationException(
                  "Unsupported operation")

            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
    }

    @JvmStatic
    fun intentFindMedia(appContext: Context, type: Int): Intent {

        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            this.type = if(type==EditorConsts.IMAGE)
                "image/*"
                   else if(type==EditorConsts.VIDEO)
                "video/*"
                   else if(type==EditorConsts.AUDIO)
                "audio/*"
                   else null

            //Verifies that an application that can handle this intent exists
            if(this.resolveActivity(appContext.packageManager)==null)
                Toast.makeText(
                  appContext,
                  "Error retrieving format!",
                  Toast.LENGTH_SHORT).show()
        }
    }

    @JvmStatic
    fun getCameraUri(appContext: Context, type: Int): Uri? {

        return if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                 getCameraUriOld(
                   appContext,
                   getImageFile() ?: return null)
               else {
                 getCameraUriNew(
                   appContext, getDirections(type))
        }
    }

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

    //TODO: consider adding a verification method to ensure no unintended files
    //  are somehow deleted
    @JvmStatic
    fun deleteImageFile(imagePath: String) = File(imagePath).delete()

    private fun getDirections(type: Int): Map<String, String> {

        return when(type) {
            EditorConsts.IMAGE -> {
                mapOf("mime" to "image/jpeg", "prefix" to IMG_PREFIX)
            }
            EditorConsts.VIDEO -> {
                mapOf("mime" to "video/mp4", "prefix" to VIDEO_PREFIX)
            }
            else -> {
                mapOf("mime" to "image/jpeg", "prefix" to IMG_PREFIX)
            }
        }
    }

    private fun getCameraUriOld(
      context: Context, file: File): Uri =
        FileProvider.getUriForFile(context, IMAGE_PROVIDER_AUTHORITY, file)

    private fun getCameraUriNew(
      context: Context, directions: Map<String, String>): Uri? {

        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME,
                directions["prefix"] + Date().getChipFileDate())
            put(MediaStore.MediaColumns.MIME_TYPE,
                directions["mime"])
            put(MediaStore.MediaColumns.RELATIVE_PATH,
                "DCIM/ChipIt")
        }

        return when(directions["prefix"]) {
            IMG_PREFIX ->
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            VIDEO_PREFIX ->
                resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            else ->
                null
        }
    }

    private fun createImageFile(storageDir: File): File {

        //TODO: consider modifying locale based on phone location
        val time = Date().getChipFileDate() //Part of the file name
        val file = File(storageDir, "$IMG_PREFIX$time.jpg")

        return file
    }

    private fun isExternalStorageAvailable(): Boolean
      = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}