package creations.rimov.com.chipit.util

import android.content.Context
import android.content.Intent
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

    //TODO: handle IOException
    @JvmStatic
    fun createImageFile(context: Context): ImageFile {
        //TODO: consider modifying locale based on phone location
        val time = SimpleDateFormat("yyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("chipit_topic_$time", ".jpg", storageDir)

        Log.i("ImageFile", "File path: ${file.absoluteFile}")

        return ImageFile(file, file.absolutePath)
    }

    //TODO: consider adding a verification method to ensure no unnecessary files are somehow deleted
    @JvmStatic
    fun deleteImageFile(imagePath: String): Boolean {

        return File(imagePath).delete()
    }

//    val addChipCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//    //Verifies that an application that can handle this intent exists
//    addChipCameraIntent.resolveActivity(activity!!.packageManager)
//
//    //TODO: handle error
//    val imageFile = try {
//        CameraUtil.createImageFile(activity!!)
//
//    } catch(e: IOException) {
//        e.printStackTrace()
//        null
//    }
//
//    if(imageFile != null) {
//        val imageUri = FileProvider.getUriForFile(activity!!,
//            CameraUtil.IMAGE_PROVIDER_AUTHORITY,
//            imageFile.file)
//
//        addChipCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
//        startActivityForResult(addChipCameraIntent, CameraUtil.CODE_TAKE_PICTURE)
//
//        if(imageFile.storagePath.isNotEmpty())
//            localViewModel.saveChip("Lorem Ipsum", imageFile.storagePath)
//    }
}