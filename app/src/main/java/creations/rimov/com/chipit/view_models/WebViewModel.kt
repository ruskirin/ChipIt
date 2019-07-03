package creations.rimov.com.chipit.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.objects.ChipReference
import creations.rimov.com.chipit.database.repos.AccessRepo

class WebViewModel : ViewModel(), AccessRepo.RepoHandler {

    private val repository = AccessRepo(DatabaseApplication.database!!, this)

    private val chip: MutableLiveData<ChipIdentity?> = MutableLiveData()

    private val parents: LiveData<List<ChipReference>> = Transformations.switchMap(chip) {
        repository.getChipReferenceParentTreeLive(it?.id)
    }
    private val children: LiveData<List<ChipCard>> = Transformations.switchMap(chip) {
        repository.getChipChildrenCardsLive(it?.id)
    }


    fun setChip(id: Long?) {

        if(id == -1L) {
            repository.setParentIdentity(null)
            return
        }

        repository.setParentIdentity(id)
    }

    fun getChip() = chip

    fun getAsChip() = chip.value?.getChip()

    fun getChipId() = chip.value?.id

    fun getParents(): LiveData<List<ChipReference>> = parents

    fun getChildren(): LiveData<List<ChipCard>> = children

    override fun <T> setData(data: T?) {

        if(data == null) {
            chip.postValue(null)
            return
        }

        if(data is ChipIdentity) chip.postValue(data)
    }

    override fun <T> setDataList(data: List<T>) {

    }
}