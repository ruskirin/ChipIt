package creations.rimov.com.chipit.extensions

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun NavController.nav(directions: NavDirections) {

    val source = directions.toString()
        .substringAfter("Action")
        .substringBefore("To")

    Log.i("NavController", "::nav(): directions ${directions}")

    if(this.currentDestination?.label.toString() != source) return

    this.navigate(directions)
}