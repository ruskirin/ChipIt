package creations.rimov.com.chipit.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.objects.ChipReference
import creations.rimov.com.chipit.database.repos.AccessRepo
import creations.rimov.com.chipit.database.repos.RepoHandler

class WebViewModel : ViewModel(), RepoHandler {

    private val repository = AccessRepo(
      DatabaseApplication.database!!, this)

    private val chip: MutableLiveData<Chip?> = MutableLiveData()

    private val parents: LiveData<List<ChipReference>> = Transformations
        .switchMap(chip) {
            repository.getChipReferenceParentTreeLive(it?.id)
    }
    private val children: LiveData<List<ChipCard>> = Transformations
        .switchMap(chip) {
            repository.getChipChildrenCardsLive(it?.id)
    }

    fun getChip() = chip

    fun setChip(id: Long) {
        repository.getChipAsync(id)
    }

    fun getParents(): LiveData<List<ChipReference>> = parents

    fun getChildren(): LiveData<List<ChipCard>> = children

    override fun <T> setData(data: T) {
        chip.postValue(data as Chip)
    }

    override fun <T> setDataList(data: List<T>) {

    }
}