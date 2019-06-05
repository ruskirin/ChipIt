package creations.rimov.com.chipit.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.activities.DirectoryActivity
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.repos.ChipChildrenRepository
import creations.rimov.com.chipit.fragments.WebFragment
import creations.rimov.com.chipit.objects.ViewModelPrompts

class WebViewModel : ViewModel(), ChipChildrenRepository.RepoChipRetriever {

    private val chipChildrenRepo = ChipChildrenRepository(DatabaseApplication.database!!, this)

    private val upperParent = MutableLiveData<ChipIdentity>()

    private val listLower = MutableLiveData<List<ChipCard>>()

    val prompts = MutableLiveData<ViewModelPrompts>()


    fun setParent(parentId: Long) {

        if(parentId == -1L) {
            Log.e("WebViewModel", "#setParent(): passed parentId == -1L")
            return
        }

        chipChildrenRepo.setParentIdentity(parentId, false)
    }

    fun getParent() = upperParent

    fun getParentId() = upperParent.value?.id ?: -1L

    fun getParentIdOfParent() = upperParent.value?.parentId ?: -1L

    fun setListLower(parentId: Long) {
        chipChildrenRepo.getChipChildrenCards(parentId, WebFragment.ListType.LOWER)
    }

    //TODO: (FUTURE) if return an empty list trigger a display saying list is empty
    fun getListUpper(): LiveData<List<ChipCard>> = chipChildrenRepo.getChipChildrenCardsLive(getParentId())

    fun getListLower(): MutableLiveData<List<ChipCard>> = listLower

    /**Insert child into horizontal chip row**/
    fun saveChip(name: String?, imgLocation: String?) {
        val chip = Chip(0, getParentId(), false, name, imgLocation ?: "", null)

        chipChildrenRepo.insertChip(chip)
    }

    fun deleteChip(chipId: Long) {
        chipChildrenRepo.deleteChip(chipId)
    }

    var chipTouchPos: Int = -1
    var chipTouchId: Long = -1L
    private var gesture: Int = -1

    fun handleUpperChipsTouch(chipAdapterPos: Int, chipId: Long) {

        when(gesture) {
            DirectoryActivity.Constants.GESTURE_DOWN -> {


            }
            DirectoryActivity.Constants.GESTURE_UP -> {

                if(chipTouchId != chipId)
                    prompts.postValue(ViewModelPrompts(true))
                else
                    prompts.postValue(ViewModelPrompts(false, true))

                this.chipTouchId = chipId
                this.chipTouchPos = chipAdapterPos
            }
            DirectoryActivity.Constants.GESTURE_LONG_TOUCH -> {


            }
        }
    }

    fun handleLowerChipsTouch(chipId: Long) {

        chipChildrenRepo.setParentIdentity(chipId, true)
    }

    /**Get the parent of upperParent and set it as the current parent (sorry for the description); navigate up the "branch" of chips**/
    fun navigateUpBranch() {

        chipChildrenRepo.setParentIdentity(getParentIdOfParent(), false)
    }

    fun setChipTouchGesture(gesture: Int) {
        this.gesture = gesture
    }

    fun resetFlags() {
        prompts.postValue(ViewModelPrompts())
    }

    override fun updateParent(parent: ChipIdentity) {
        upperParent.postValue(parent)
    }

    override fun setChipList(chips: List<ChipCard>, type: Int) {

        if(type == WebFragment.ListType.LOWER)
            listLower.postValue(chips)
    }
}