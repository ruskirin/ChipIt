package creations.rimov.com.chipit.data.repos

import creations.rimov.com.chipit.data.ChipDatabase
import creations.rimov.com.chipit.data.objects.Chip

class EditRepo(
  chipDb: ChipDatabase,
  private val handler: AsyncHandler) {

    private val dao = chipDb.chipDao()

    fun getChip(id: Long) {
        DbAsyncTasks.AsyncGetChip(dao, handler).execute(id)
    }

    fun updateChip(chip: Chip) {
        DbAsyncTasks.AsyncUpdateChip(dao).execute(chip)
    }

    /**
     * Very important to include the parentId and counter in chip,
     *   in order to update the counters of parents appropriately
     * **/
    fun insertChip(chip: Chip) {
        DbAsyncTasks.AsyncInsertChip(dao).execute(chip)
    }

    /**
     * Very important to include the parentId and counter in chip,
     *   in order to update the counters of parents appropriately
     * **/
    fun deleteChip(chip: Chip) {
        DbAsyncTasks.AsyncDeleteChipTree(dao).execute(chip)
    }
}