package creations.rimov.com.chipit.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipReference
import creations.rimov.com.chipit.objects.ChipAction

class GlobalViewModel : ViewModel() {
    //Used by Main to work in tandem with Web's transitions
    private val webTransition: MutableLiveData<Float> = MutableLiveData()

    private val chipAction: MutableLiveData<ChipAction> = MutableLiveData()

    private val webParents: MutableLiveData<List<ChipReference>> = MutableLiveData()

    var observedChipId: Long? = null


    fun getWebTransition() = webTransition

    fun setWebTransition(progress: Float) {
        webTransition.postValue(progress)
    }

    fun getWebParents() = webParents

    fun setWebParents(parents: List<ChipReference>) {
        webParents.postValue(parents)
    }

    fun getChipAction() = chipAction

    fun setChipAction(chip: ChipAction) {
        chipAction.postValue(chip)
    }
}