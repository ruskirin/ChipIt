package creations.rimov.com.chipit.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.objects.ChipReference
import creations.rimov.com.chipit.objects.ChipAction

class GlobalViewModel : ViewModel() {

    //Chip + requested action (edit, delete, update)
    private val chipEdit: MutableLiveData<ChipAction> = MutableLiveData()

    //Currently displayed Chip and its parents, used by toolbar
    private val webParents: MutableLiveData<List<ChipReference>> = MutableLiveData()

    //Chip selected in the parent spinner
    private val webSelectedId: MutableLiveData<Long?> = MutableLiveData()

    var observedChipId: Long? = null


    fun getChipEdit() = chipEdit

    fun setChipEdit(chip: ChipAction) {
        chipEdit.postValue(chip)
    }

    fun getWebParents() = webParents

    fun setWebParents(parents: List<ChipReference>) {
        webParents.postValue(parents)
    }

    fun getWebSelectedId() = webSelectedId

    fun setWebSelectedId(id: Long?) {
        if(id != webSelectedId.value) webSelectedId.postValue(id)
    }
}