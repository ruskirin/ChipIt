package creations.rimov.com.chipit.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.objects.ChipUpdateBasic
import creations.rimov.com.chipit.database.repos.EditRepo
import creations.rimov.com.chipit.objects.CoordPoint

class EditorViewModel : ViewModel() {

    private val repository = EditRepo(DatabaseApplication.database!!)

    var editingChip: ChipUpdateBasic? = null
    var isEditing = false
    var isNew = false //Creating a new Chip or updating an existing one?


    //Starting to startEdit chip, set all the flags and hold a copy of its info
    fun startEdit(chip: Chip) {

        isEditing = true
        isNew = false

        editingChip = ChipUpdateBasic.instance(chip.id, chip.parentId, chip.isTopic, chip.name, chip.desc, chip.imgLocation)
    }
    //Starting to create a new Chip, set all the flags and hold a copy of its info
    fun startCreate(isTopic: Boolean, parentId: Long?, vertices: MutableList<CoordPoint>?) {

        isEditing = true
        isNew = true

        editingChip = ChipUpdateBasic.instance(0L, parentId, isTopic, vertices = vertices)
    }
    //Finished editing; update flags and update the DB
    fun saveEdit() {

        isEditing = false

        if(isNew) {
            Log.i("Chip Creation", "EditorVM#saveEdit(): saving ${editingChip?.name}, desc: ${editingChip?.desc}")

            insertChip(editingChip?.toChip() ?: return)
            return
        }

        updateChipBasic(editingChip ?: return)
    }

    fun setName(text: String) {
        editingChip?.let { it.name = text }
    }

    fun setDesc(text: String) {
        editingChip?.let { it.desc = text }
    }

    fun updateChipBasic(chipInfo: ChipUpdateBasic) {
        repository.updateChipBasic(chipInfo)
    }

    /**Insert a Chip into the database "chips"**/
    fun insertChip(chip: Chip) {
        repository.insertChip(chip)
    }

    fun deleteChip(chip: Chip) {
        repository.deleteChip(chip)
    }
}