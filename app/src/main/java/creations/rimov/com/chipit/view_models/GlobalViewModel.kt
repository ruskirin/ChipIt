package creations.rimov.com.chipit.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipUpdateBasic
import creations.rimov.com.chipit.database.repos.EditRepo

class GlobalViewModel : ViewModel() {

    private val repository = EditRepo(DatabaseApplication.database!!)

    private var observedChipId: Long = -1L

    private val chipToEdit: MutableLiveData<Chip> = MutableLiveData()


    fun getObservedChipId() = observedChipId

    fun setObservedChipId(id: Long) {
        observedChipId = id
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
    fun insertChip(parentId: Long?,
                   isTopic: Boolean = false,
                   name: String = "",
                   desc: String = "",
                   counter: Int = 0,
                   imgLocation: String = "") {

        val chip = Chip(
            0L,
            parentId,
            isTopic,
            name,
            desc,
            counter = counter,
            imgLocation = imgLocation)

        repository.insertChip(chip)
    }

    fun deleteChip(id: Long, parentId: Long?, counter: Int) {
        repository.deleteChip(Chip(id, parentId, counter = counter))
    }
}