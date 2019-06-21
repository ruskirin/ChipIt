package creations.rimov.com.chipit.database.repos

import android.util.Log
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.objects.Chip

class EditRepo(chipDb: ChipDatabase) {

    private val dao = chipDb.editDao()

    fun updateChip(chip: Chip) {
        dao.updateChip(chip)
    }

    fun insertChip(chip: Chip) {
        DbAsyncTasks.InsertChip(dao).execute(chip)
    }
}