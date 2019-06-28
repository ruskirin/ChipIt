package creations.rimov.com.chipit.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.repos.AccessRepo

class WebViewModel : ViewModel(), AccessRepo.RepoHandler {

    private val repository = AccessRepo(DatabaseApplication.database!!, this)

    private val parentUpper: MutableLiveData<ChipIdentity> = MutableLiveData()

    private val children: LiveData<List<ChipCard>> = Transformations.switchMap(parentUpper) {
        repository.getChipChildrenCardsLive(it.id)
    }


    fun setParent(id: Long) {
        //TODO ERROR: handle
        if(id == -1L) {
            Log.e("WebViewModel", "#setParentIdentity(): passed id == -1L")
            return
        }

        repository.setParentIdentity(id, false)
    }

    fun getParent() = parentUpper

    fun getParentAsChip() = parentUpper.value?.getChip()

    fun getParentId() = parentUpper.value?.id ?: -1L

    fun getChildren(): LiveData<List<ChipCard>> = children

    override fun <T> setData(data: T) {
        if(data is ChipIdentity) parentUpper.postValue(data)
    }

    override fun <T> setDataList(data: List<T>) {

    }
}