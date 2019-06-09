package creations.rimov.com.chipit.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.activities.DirectoryActivity
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.repos.WebRepository
import creations.rimov.com.chipit.fragments.WebFragment
import creations.rimov.com.chipit.objects.ViewModelPrompts

class WebViewModel : ViewModel(), WebRepository.WebRepoHandler {

    private val repository = WebRepository(DatabaseApplication.database!!, this)

    private val parentUpper = MutableLiveData<ChipIdentity>()

    private val listLower = MutableLiveData<List<ChipCard>>()

    val prompts = MutableLiveData<ViewModelPrompts>()


    fun setParent(parentId: Long) {

        if(parentId == -1L) {
            Log.e("WebViewModel", "#setParentIdentity(): passed parentId == -1L")
            return
        }

        repository.setParentIdentity(parentId, false)
    }

    fun getParent() = parentUpper

    fun getParentId() = parentUpper.value?.id ?: -1L

    fun getParentIdOfParent() = parentUpper.value?.parentId ?: -1L

    fun setListLower(parentId: Long) {
        repository.getChipChildrenCards(parentId, WebFragment.ListType.LOWER)
    }

    //TODO: (FUTURE) if return an empty list trigger a display saying list is empty
    fun getListUpper(): LiveData<List<ChipCard>> = Transformations.switchMap(parentUpper) {
        repository.getChipChildrenCardsLive(it.id)
    }

    fun getListLower(): MutableLiveData<List<ChipCard>> = listLower

    /**Insert child into horizontal chip row**/
    fun saveChip(name: String?, imgLocation: String?) {
        val chip = Chip(0, getParentId(), false, name, imgLocation ?: "", null)

        repository.insertChip(chip)
    }

    fun deleteChip(chipId: Long) {
        repository.deleteChip(chipId)
    }

    var chipTouchPos: Int = -1
    var chipTouchId: Long = -1L
    private var gesture: Int = -1

    fun handleUpperChipsTouch(chipAdapterPos: Int, chipId: Long) {

        when(gesture) {
            DirectoryActivity.Constants.GESTURE_UP -> {

                if(chipTouchId != chipId)
                    prompts.postValue(ViewModelPrompts(true))
                else
                    prompts.postValue(ViewModelPrompts(false, true))

                this.chipTouchId = chipId
                this.chipTouchPos = chipAdapterPos
            }
        }
    }

    fun handleLowerChipsTouch(chipId: Long) {

        repository.setParentIdentity(chipId, true)
    }

    /**Get the parent of parentUpper and set it as the current parent (sorry for the description); navigate up the "branch" of chips**/
    fun navigateUpBranch() {

        repository.setParentIdentity(getParentIdOfParent(), false)
    }

    fun setChipTouchGesture(gesture: Int) {
        this.gesture = gesture
    }

    fun resetFlags() {
        prompts.postValue(ViewModelPrompts())
    }

    override fun updateParent(parent: ChipIdentity) {
        parentUpper.postValue(parent)
    }

    override fun setChipList(chips: List<ChipCard>, type: Int) {

        if(type == WebFragment.ListType.LOWER)
            listLower.postValue(chips)
    }
}