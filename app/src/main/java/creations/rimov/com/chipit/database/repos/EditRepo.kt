package creations.rimov.com.chipit.database.repos

import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.objects.ChipUpdateBasic

class EditRepo(chipDb: ChipDatabase) {

    private val dao = chipDb.chipDao()

    fun updateChipBasic(chip: ChipUpdateBasic) {
        DbAsyncTasks.AsyncChipUpdate(dao).execute(chip)
    }

    /**Very important to include the parentId and counter in chip, in order to update the counters of parents appropriately**/
    fun insertChip(chip: Chip) {
        DbAsyncTasks.InsertChip(dao).execute(chip)
    }

    /**Very important to include the parentId and counter in chip, in order to update the counters of parents appropriately**/
    fun deleteChip(chip: Chip) {
        DbAsyncTasks.DeleteChipTree(dao).execute(chip)
    }
}