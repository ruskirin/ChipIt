package creations.rimov.com.chipit.extensions

import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun NavController.nav(directions: NavDirections) {

    val source = directions.toString()
        .substringAfter("Action")
        .substringBefore("To")

    Log.i("NavController", "::nav(): directions ${directions}")

    if(this.currentDestination?.label.toString() != source) {
        Log.e("NavController", "#nav(): failed to navigate! " +
                               "Incorrect source destination")
        return
    }

    this.navigate(directions)
}