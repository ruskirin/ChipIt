package creations.rimov.com.chipit.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.repos.EditRepo

class GlobalViewModel : ViewModel() {

    private val repository = EditRepo(DatabaseApplication.database!!)

    var chipFragParentId: Long = 0L

    private val chipToEdit: MutableLiveData<Chip> = MutableLiveData()
    private val albumChip: MutableLiveData<ChipIdentity> = MutableLiveData()

    fun getChipToEdit() = chipToEdit

    fun setChipToEdit(chip: Chip) {
        chipToEdit.postValue(chip)
    }

    fun getAlbumChip() = albumChip

    fun setAlbumChip(chip: ChipIdentity) {
        albumChip.postValue(chip)
    }

    fun updateChip(chip: Chip) {
        repository.updateChip(chip)
    }

    /**Insert a Chip into the database "chips"**/
    fun insertChip(chip: Chip) {

        val save = Chip(
            0L,
            chip.parentId,
            chip.isTopic,
            chip.name,
            chip.desc)

        repository.insertChip(save)
    }

    fun deleteChip(chip: Chip) {
        repository.deleteChip(chip)
    }
}