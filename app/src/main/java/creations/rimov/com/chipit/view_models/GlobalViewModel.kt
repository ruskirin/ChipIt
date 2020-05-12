package creations.rimov.com.chipit.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipReference

class GlobalViewModel : ViewModel() {

    private val focusChip: MutableLiveData<Chip?> = MutableLiveData()
    private val action: MutableLiveData<Int> = MutableLiveData()

    private val webTransition: MutableLiveData<Boolean> = MutableLiveData()
    private val webParents: MutableLiveData<List<ChipReference>> = MutableLiveData()

    fun getFocusChip() = focusChip

    fun getFocusId() = focusChip.value?.id

    fun getFocusImgPath() = focusChip.value?.matPath

    fun setFocusChip(chip: Chip?) {
        if(chip != focusChip.value) focusChip.postValue(chip)
    }

    fun getAction() = action

    fun setAction(action: Int) {
        this.action.postValue(action)
    }

    fun getWebTransition() = webTransition

    fun setWebTransition(forward: Boolean) {
        if(forward != webTransition.value) webTransition.postValue(forward)
    }

    fun getWebParents() = webParents

    fun setWebParents(parents: List<ChipReference>) {
        webParents.postValue(parents)
    }
}