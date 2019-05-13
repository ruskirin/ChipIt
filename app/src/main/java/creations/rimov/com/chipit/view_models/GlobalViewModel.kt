package creations.rimov.com.chipit.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GlobalViewModel : ViewModel() {

    //Observed flag triggering FAB action
    val fabTouched = MutableLiveData<Boolean>(false)

    fun setFabTouched(touched: Boolean) {
        fabTouched.postValue(touched)
    }
}