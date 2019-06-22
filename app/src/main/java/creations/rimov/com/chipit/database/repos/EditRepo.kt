package creations.rimov.com.chipit.database.repos

import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.objects.Chip

class EditRepo(chipDb: ChipDatabase) {

    private val dao = chipDb.chipDao()

    fun updateChip(chip: Chip) {
        DbAsyncTasks.AsyncChipUpdate(dao).execute(chip)
    }

    fun insertChip(chip: Chip) {
        DbAsyncTasks.InsertChip(dao).execute(chip)
    }

    fun deleteChip(chip: Chip) {
        DbAsyncTasks.DeleteChipTree(dao).execute(chip)
    }
}