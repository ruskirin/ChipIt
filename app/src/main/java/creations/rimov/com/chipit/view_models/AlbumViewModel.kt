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
import creations.rimov.com.chipit.database.repos.AccessRepo
import creations.rimov.com.chipit.objects.ViewModelPrompts
import java.text.SimpleDateFormat
import java.util.*

class AlbumViewModel : ViewModel(), AccessRepo.RepoHandler {

    private val repository = AccessRepo(DatabaseApplication.database!!, this)

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

    override fun <ChipIdentity> setData(data: ChipIdentity) {
        parentUpper.postValue(data as creations.rimov.com.chipit.database.objects.ChipIdentity)
    }
}