package creations.rimov.com.chipit.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.repos.ChipChildrenRepository
import creations.rimov.com.chipit.fragments.WebFragment
import creations.rimov.com.chipit.objects.RecyclerTouchFlag

class WebViewModel : ViewModel(), ChipChildrenRepository.RepoChipRetriever {

    private val chipChildrenRepo = ChipChildrenRepository(DatabaseApplication.database!!, this)

    private var parentId: Long = 0L

    private lateinit var chipsHorizontal: LiveData<List<ChipCard>>
    private val chipsVertical: MutableLiveData<List<ChipCard>> = MutableLiveData()

    val chipTouch = MutableLiveData<RecyclerTouchFlag>()


    fun initHorizontalChips(parentId: Long) {
        this.parentId = parentId

        chipsHorizontal = chipChildrenRepo.getChipChildrenCards(parentId)
    }

    fun initVerticalChips(parentId: Long) {
        Log.i("RecyclerView", "WebVM#initVerticalChips(): initializing children of $parentId")

        chipChildrenRepo.getChipChildrenCardsTwo(parentId)
    }

    fun getParentId() = parentId

    //TODO: (FUTURE) if return an empty list trigger a display saying list is empty
    fun getChipsHorizontal(): LiveData<List<ChipCard>>? =
        if(::chipsHorizontal.isInitialized)
            chipsHorizontal
        else
            null

    fun getChipsVertical(): LiveData<List<ChipCard>>? = chipsVertical

    /*if(::chipsVertical.isInitialized)
            chipsVertical
        else
            null*/

    /**Insert child into horizontal chip row**/
    fun saveChip(name: String?, imgLocation: String?) {
        val chip = Chip(0, parentId, false, name, imgLocation ?: "", null)

        chipChildrenRepo.insertChip(chip)
    }

    /**Return the chip in the specified listType at the specified position**/
    fun getChipAtPosition(listType: Int, position: Int) =
        if(listType == WebFragment.Constant.HORIZONTAL_CHIP_LIST)
            chipsHorizontal.value?.get(position)
        else
            null

    fun setChipTouch() {
        chipTouch.postValue(RecyclerTouchFlag(true))
    }

    fun setChipLongTouch() {
        chipTouch.postValue(RecyclerTouchFlag(false, true))
    }

    //TODO NOW: vertical chips are now displayed. Change the layout to the new version
    override fun setChipList(chips: List<ChipCard>) {
        chipsVertical.postValue(chips)
    }
}