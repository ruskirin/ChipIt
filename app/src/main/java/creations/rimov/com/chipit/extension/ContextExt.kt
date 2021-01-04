package creations.rimov.com.chipit.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * Goes through context to find one that is an Activity and returns it.
 * Kotlin implementation found here:
 *   https://stackoverflow.com/a/58249983/13557629
 */
fun Context?.activity(): Activity? =
    when(this) {
        is Activity -> this
        else -> (this as? ContextWrapper)?.baseContext?.activity()
}