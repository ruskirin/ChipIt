package creations.rimov.com.chipit.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.repos.AsyncHandler
import creations.rimov.com.chipit.database.repos.EditRepo

class EditorViewModel(handler: AsyncHandler) : ViewModel() {

    private val repository = EditRepo(DatabaseApplication.database!!, handler)

    var chipBackup: Chip? = null

    fun getChip(chipId: Long) {
        repository.getChip(chipId)
    }

    fun saveNew(chip: Chip) {
        Log.i("EditorVM", "::saveNew(): saving new chip $chip)")
        repository.insertChip(chip)
    }
    //Finished editing; update flags and update the DB
    fun saveEdit(chip: Chip) {
        Log.i("EditorVM", "::saveEdit(): updating chip $chip)")
        repository.updateChip(chip)
    }

    fun deleteChip(chip: Chip) {
        repository.deleteChip(chip)
    }
}