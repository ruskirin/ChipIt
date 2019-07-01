package creations.rimov.com.chipit.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipReference
import creations.rimov.com.chipit.database.objects.ChipUpdateBasic
import creations.rimov.com.chipit.database.repos.EditRepo

class GlobalViewModel : ViewModel() {

    private val repository = EditRepo(DatabaseApplication.database!!)

    private val chipToEdit: MutableLiveData<Chip> = MutableLiveData()

    private var observedChipId: Long = -1L

    private val webParents: MutableLiveData<List<ChipReference>> = MutableLiveData()


    fun getObservedChipId() = observedChipId

    fun setObservedChipId(id: Long) {
        observedChipId = id
    }

    fun getWebParents() = webParents

    fun setWebParents(parents: List<ChipReference>) {
        webParents.postValue(parents)
    }

    fun getChipToEdit() = chipToEdit

    fun setChipToEdit(chip: Chip) {
        chipToEdit.postValue(chip)
    }

    fun updateChipBasic(id: Long, name: String, desc: String, imgLocation: String) {

        val chip = ChipUpdateBasic.set(id, name, desc, imgLocation)

        repository.updateChipBasic(chip)
    }

    /**Insert a Chip into the database "chips"**/
    fun insertChip(chip: Chip) {
        repository.insertChip(chip)
    }

    fun deleteChip(chip: Chip) {
        repository.deleteChip(chip)
    }
}