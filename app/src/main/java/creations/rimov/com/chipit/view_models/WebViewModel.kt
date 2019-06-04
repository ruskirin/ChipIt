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

    private lateinit var upperParent: ChipIdentity

    private val listLower = MutableLiveData<List<ChipCard>>()

    val prompts = MutableLiveData<ViewModelPrompts>()


    fun setParent(parentId: Long) {

        Log.i("Life Event", "WebViewModel#setParent(): passed parent id: $parentId")

        if(parentId == -1L) {
            Log.e("WebViewModel", "#setParent(): passed parentId == -1L")
            return
        }

        chipChildrenRepo.setParentIdentity(parentId, false)
    }

    fun isParentTopic() = upperParent.isTopic

    fun getParentId() =
        if(::upperParent.isInitialized) upperParent.id
        else -1L

    fun setListLower(parentId: Long) {
        chipChildrenRepo.getChipChildrenCards(parentId, WebFragment.ListType.LOWER)
    }

    //TODO: (FUTURE) if return an empty list trigger a display saying list is empty
    fun getListUpper(): LiveData<List<ChipCard>> = chipChildrenRepo.getChipChildrenCardsLive(getParentId())

    fun getListLower() = listLower

    /**Insert child into horizontal chip row**/
    fun saveChip(name: String?, imgLocation: String?) {
        val chip = Chip(0, getParentId(), false, name, imgLocation ?: "", null)

        chipChildrenRepo.insertChip(chip)
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

    fun navigateUpBranch() {

//        chipChildrenRepo.setParentIdentity(getParentId(), )
    }

    fun setChipTouchGesture(gesture: Int) {
        this.gesture = gesture
    }

    fun resetFlags() {
        prompts.postValue(ViewModelPrompts())
    }

    override fun updateParent(parent: ChipIdentity) {
        upperParent = parent
    }

    override fun setChipList(chips: List<ChipCard>, type: Int) {

        if(type == WebFragment.ListType.LOWER)
            listLower.postValue(chips)
    }
}