package creations.rimov.com.chipit.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import creations.rimov.com.chipit.util.VMFactory

/**
 * See link for more detail:
 *   https://proandroiddev.com/view-model-creation-in-android-android-architecture-components-kotlin-ce9f6b93a46b
 */
inline fun <reified T : ViewModel> Fragment.getViewModel(
  noinline factory: (() -> T)? = null): T {

    return when(factory) {
        null -> ViewModelProvider(this).get(T::class.java)
        else -> ViewModelProvider(this, VMFactory(factory)).get(T::class.java)
    }
}