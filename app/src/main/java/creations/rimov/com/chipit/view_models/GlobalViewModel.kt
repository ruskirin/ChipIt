package creations.rimov.com.chipit.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipReference

class GlobalViewModel : ViewModel() {

    //Primary chip currently being viewed
    private val focusChip: MutableLiveData<Chip?> = MutableLiveData()

    //Flags for different scenarios
    private val action: MutableLiveData<Int> = MutableLiveData(0)
    private val editAction: MutableLiveData<Int> = MutableLiveData()

    //Backup slot for chips
    private var bufferChip: Chip? = null

    private val webTransition: MutableLiveData<Boolean> = MutableLiveData()
    private val webParents: MutableLiveData<List<ChipReference>> = MutableLiveData()

    fun getFocusChip() = focusChip

    fun setFocusChip(chip: Chip?, save: Boolean) {

        if(chip != focusChip.value) {
            if(save) bufferChip = focusChip.value

            Log.i("GlobalVM", "::setFocusChip(): changing focusChip to " +
                              "\"${chip?.name}\", bufferChip \"${bufferChip?.name}\"")

            focusChip.postValue(chip)
        }
    }

    fun getFocusId() = focusChip.value?.id

    fun getFocusImgPath() = focusChip.value?.matPath

    fun setName(text: String) {
        Log.i("GlobalVM", "::setName(): changing name to $text")

        focusChip.postValue(
          focusChip.value?.copy(name = text))
    }

    fun setDesc(text: String) {
        Log.i("GlobalVM", "::setDesc(): changing desc to $text")

        focusChip.postValue(
          focusChip.value?.copy(desc = text))
    }

    fun setMat(matType: Int, matPath: String) {

        focusChip.postValue(
          focusChip.value?.copy(matType = matType, matPath = matPath))
    }

    fun loadBufferChip() {
        Log.i("GlobalVM", "::loadSavedChip(): loading " +
                          "chip \"${bufferChip?.name}\"")

        focusChip.postValue(bufferChip)
        bufferChip = null
    }

    fun getAction() = action

    fun setAction(action: Int) {
        this.action.postValue(action)
    }

    fun getEditAction() = editAction

    fun setEditAction(action: Int) {
        Log.i("GlobalVM", "::setEditAction(): setting action $action")

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