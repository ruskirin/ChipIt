package creations.rimov.com.chipit.view_models

import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.repos.EditRepo
import creations.rimov.com.chipit.objects.CoordPoint

class EditorViewModel : ViewModel() {

    private val repository = EditRepo(DatabaseApplication.database!!)

    var editingChip: Chip? = null

    //Starting to startEdit chip, set all the flags and hold a copy of its info
    fun startEdit(chip: Chip) {
        editingChip = chip
    }
    //Starting to create a new Chip, set all the flags and hold a copy of its info
    fun startCreate(parentId: Long?, vertices: MutableList<CoordPoint>?) {
        editingChip = Chip(0L, parentId, vertices = vertices)
    }

    fun saveNew(): Boolean {
        repository.insertChip(editingChip ?: return false)
        return true
    }
    //Finished editing; update flags and update the DB
    fun saveEdit(): Boolean {
        repository.updateChip(editingChip ?: return false)
        return true
    }

    fun setName(text: String) {
        editingChip = editingChip?.copy(name = text)
    }

    fun setDesc(text: String) {
        editingChip = editingChip?.copy(desc = text)
    }

    fun setMat(matType: Int, matPath: String) {
        editingChip = editingChip?.copy(matType = matType, matPath = matPath)
    }

    fun deleteChip(chip: Chip) {
        repository.deleteChip(chip)
    }
}