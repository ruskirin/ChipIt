package creations.rimov.com.chipit.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.data.objects.Chip
import creations.rimov.com.chipit.data.objects.ChipReference

class MainViewModel : ViewModel() {

    //Pair(width, height)
    var screenDimen: Pair<Int, Int> = Pair(0, 0)

    //Primary chip currently being viewed
    private val focusChip: MutableLiveData<Chip?> = MutableLiveData()
    //Backup slot for chips
    private var bufferChip: Chip? = null

    //Flags for different scenarios
    private val editAction: MutableLiveData<Int> = MutableLiveData()

    private val webTransition: MutableLiveData<Boolean> = MutableLiveData()
    private val webParents: MutableLiveData<List<ChipReference>> = MutableLiveData()

    fun getFocusChip() = focusChip

    fun setFocusChip(chip: Chip?, save: Boolean) {

        if(chip != focusChip.value) {
            if(save) bufferChip = focusChip.value

            Log.i("CommsVM",
                  "#setFocusChip(): focusChip $chip " +
                  "bufferChip $bufferChip")

            focusChip.postValue(chip)
        }
    }

    fun getFocusId() = focusChip.value?.id

    fun setName(text: String) {
        Log.i("CommsVM", "::setName(): changing name to $text")

        focusChip.postValue(
          focusChip.value?.copy(name = text))
    }

    fun setDesc(text: String) {
        Log.i("CommsVM", "::setDesc(): changing desc to $text")

        focusChip.postValue(
          focusChip.value?.copy(desc = text))
    }

    fun setMat(matType: Int, matPath: String) {

        focusChip.postValue(
          focusChip.value?.copy(matType = matType, matPath = matPath))
    }

    fun loadBufferChip() {
        Log.i("CommsVM", "::loadSavedChip(): loading " +
                          "chip $bufferChip")

        focusChip.postValue(bufferChip)
        bufferChip = null
    }

    fun getEditAction() = editAction

    fun setEditAction(action: Int) {
        Log.i("CommsVM", "::setEditAction(): setting action $action")

        editAction.postValue(action)
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