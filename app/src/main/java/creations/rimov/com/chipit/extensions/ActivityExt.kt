package creations.rimov.com.chipit.extensions

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import creations.rimov.com.chipit.activities.MainActivity
import creations.rimov.com.chipit.util.CameraUtil

//Since SDK 23(24?), permission must be requested at runtime if it has not
// already been granted
fun Activity.getStorageWritePermission(): Boolean {

    if(ContextCompat.checkSelfPermission(
        this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
      == PackageManager.PERMISSION_GRANTED) {
        return true //Permission has already been granted
    }

    //Explain why you need the permission
    if(ActivityCompat.shouldShowRequestPermissionRationale(
        this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        //TODO FUTURE: display rationale for this request
        Toast.makeText(this, "Permission required for app operation",
                       Toast.LENGTH_SHORT).show()
    }

    //Permission has not yet been granted, check onRequestPermissionResult()
    ActivityCompat.requestPermissions(
      this,
      arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
      MainActivity.Constant.REQUEST_WRITE_EXTERNAL_STORAGE)

    return false
}