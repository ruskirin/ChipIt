package creations.rimov.com.chipit.view_models

import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.repos.ChipChildrenRepository
import creations.rimov.com.chipit.fragments.WebFragment
import creations.rimov.com.chipit.objects.WebViewModelPrompts

class WebViewModel : ViewModel(), ChipChildrenRepository.RepoChipRetriever {

    private val chipChildrenRepo = ChipChildrenRepository(DatabaseApplication.database!!, this)

    private var parentId: Long = 0L

    private val listUpper: MutableLiveData<List<ChipCard>> = MutableLiveData()
    private val listLower: MutableLiveData<List<ChipCard>> = MutableLiveData()

    val prompts = MutableLiveData<WebViewModelPrompts>()


    fun setUpperList(parentId: Long) {
        this.parentId = parentId

        chipChildrenRepo.getChipChildrenCards(parentId, WebFragment.Constants.LIST_UPPER)
    }

    fun setLowerList(parentId: Long) {
        Log.i("RecyclerView", "WebVM#setLowerList(): initializing children of $parentId")

        chipChildrenRepo.getChipChildrenCards(parentId, WebFragment.Constants.LIST_LOWER)
    }

    fun getParentId() = parentId

    //TODO: (FUTURE) if return an empty list trigger a display saying list is empty
    fun getListUpper(): LiveData<List<ChipCard>>? = listUpper

    fun getListLower(): LiveData<List<ChipCard>>? = listLower

    /**Return the chip in the specified listType at the specified position**/
    fun getChipAtPosition(listType: Int, position: Int) =
        if(listType == WebFragment.Constants.LIST_UPPER)
            listUpper.value?.get(position)
        else
            null

    /**Insert child into horizontal chip row**/
    fun saveChip(name: String?, imgLocation: String?) {
        val chip = Chip(0, parentId, false, name, imgLocation ?: "", null)

        chipChildrenRepo.insertChip(chip)
    }

    fun handleTouchEvent(chipAdapterPos: Int, chipId: Long) {

        //TODO NOW: continue with this
    }

    fun handleChipGesture(gesture: Int) {

        when(gesture) {

            WebFragment.Constants.GESTURE_TOUCH -> {


            }
            WebFragment.Constants.GESTURE_LONG_TOUCH -> {


            }
        }
    }

    override fun setChipList(chips: List<ChipCard>, type: Int) {

        if(type == WebFragment.Constants.LIST_UPPER)
            listUpper.postValue(chips)
        else
            listLower.postValue(chips)
    }
}