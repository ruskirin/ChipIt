package creations.rimov.com.chipit.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GlobalViewModel : ViewModel() {

    var chipFragParentId: Long = 0L

    //Observed flag triggering FAB action
    val fabTouched = MutableLiveData<Boolean>(false)

    fun setFabTouched(touched: Boolean) {
        fabTouched.postValue(touched)
    }

    fun saveChipFragParentId(id: Long) {
        chipFragParentId = id
    }
}