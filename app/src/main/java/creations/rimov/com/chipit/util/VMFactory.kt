package creations.rimov.com.chipit.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * generic ViewModel factory which works by taking the input of another *specific*
 *   factory instance
 *
 * @param vm: ViewModel instance with desired parameters
 */
class VMFactory<T>(private val vm: () -> T)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return vm() as T
    }
}