package creations.rimov.com.chipit.view_models

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

    private val focusId: MutableLiveData<Long?> = MutableLiveData()

    private val chip: LiveData<ChipIdentity?> = Transformations.switchMap(focusId) {
        repository.getChipIdentityLive(it)
    }
    private val parents: LiveData<List<ChipReference>> = Transformations.switchMap(focusId) {
        repository.getChipReferenceParentTreeLive(it)
    }
    private val children: LiveData<List<ChipCard>> = Transformations.switchMap(focusId) {
        repository.getChipChildrenCardsLive(it)
    }


    fun setFocusId(id: Long?) {

        if(id == -1L) {
            focusId.postValue(null)
            return
        }

        focusId.postValue(id)
    }

    fun getChip() = chip

    fun getAsChip() = chip.value?.asChip()

    fun getChipId() = chip.value?.id

    fun getChipImg() = chip.value?.repPath

    fun getParents(): LiveData<List<ChipReference>> = parents

    fun getChildren(): LiveData<List<ChipCard>> = children

    override fun <T> setData(data: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> setDataList(data: List<T>) {

    }
}