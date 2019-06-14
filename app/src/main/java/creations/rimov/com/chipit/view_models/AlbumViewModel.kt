package creations.rimov.com.chipit.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.activities.MainActivity
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.repos.AlbumRepository
import creations.rimov.com.chipit.fragments.AlbumFragment
import creations.rimov.com.chipit.objects.ViewModelPrompts

class AlbumViewModel : ViewModel(), AlbumRepository.WebRepoHandler {

    private val repository = AlbumRepository(DatabaseApplication.database!!, this)

    private val parentUpper = MutableLiveData<ChipIdentity>()

    val prompts = MutableLiveData<ViewModelPrompts>()


    fun setParent(parentId: Long) {

        if(parentId == -1L) {
            Log.e("AlbumViewModel", "#setParentIdentity(): passed parentId == -1L")
            return
        }

        repository.setParentIdentity(parentId, false)
    }

    fun getParent() = parentUpper

    fun getParentId() = parentUpper.value?.id ?: -1L

    fun getParentIdOfParent() = parentUpper.value?.parentId ?: -1L

    //TODO: (FUTURE) if return an empty list trigger a display saying list is empty
    fun getChips(): LiveData<List<ChipCard>> = Transformations.switchMap(parentUpper) {
        repository.getChipChildrenCardsLive(it.id)
    }

    /**Insert child into horizontal chip row**/
    fun saveChip(name: String, imgLocation: String?) {
        val chip = Chip(0, getParentId(), false, name, imgLocation ?: "", null)

        repository.insertChip(chip)
    }

    fun deleteChip(chipId: Long) {
        repository.deleteChipAndChildren(chipId)
    }

    fun handleChipGesture(gesture: Int) {

        when(gesture) {

            MainActivity.Constants.GESTURE_UP -> {
                prompts.postValue(ViewModelPrompts(toNextScreen = true))
            }

            MainActivity.Constants.GESTURE_LONG_TOUCH -> {
                prompts.postValue(ViewModelPrompts(true))
            }
        }
    }

    /**Get the parent of parentUpper and set it as the current parent (sorry for the description); navigate up the "branch" of chips**/
    fun navigateUpBranch() {

        repository.setParentIdentity(getParentIdOfParent(), false)
    }

    override fun updateParent(parent: ChipIdentity) {
        parentUpper.postValue(parent)
    }
}