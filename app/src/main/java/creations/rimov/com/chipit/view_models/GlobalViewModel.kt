package creations.rimov.com.chipit.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipReference
import creations.rimov.com.chipit.objects.ChipAction

class GlobalViewModel : ViewModel() {

    private val webTransition: MutableLiveData<Boolean> = MutableLiveData()

    private val chipAction: MutableLiveData<ChipAction> = MutableLiveData()

    private val webParents: MutableLiveData<List<ChipReference>> = MutableLiveData()

    private val webSelectedId: MutableLiveData<Long?> = MutableLiveData()

    var observedChipId: Long? = null


    fun getWebTransition() = webTransition

    fun setWebTransition(forward: Boolean) {
        if(forward != webTransition.value) webTransition.postValue(forward)
    }

    fun getWebParents() = webParents

    fun setWebParents(parents: List<ChipReference>) {
        webParents.postValue(parents)
    }

    fun getChipAction() = chipAction

    fun setChipAction(chip: ChipAction) {
        chipAction.postValue(chip)
    }

    fun getWebSelectedId() = webSelectedId

    fun setWebSelectedId(id: Long?) {
        if(id != webSelectedId.value) webSelectedId.postValue(id)
    }
}