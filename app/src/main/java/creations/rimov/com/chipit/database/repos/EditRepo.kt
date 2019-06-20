package creations.rimov.com.chipit.database.repos

import android.util.Log
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.objects.Chip

class EditRepo(chipDb: ChipDatabase) {

    private val dao = chipDb.editDao()

    fun insertChip(chip: Chip) {

        if(chip.parentId == -1L && !chip.isTopic) {
            Log.e("TopicChipRepo", "#insertChip: can't insert a chip that is not a name and has parent id == -1L")
            return
        }

        DbAsyncTasks.InsertChip(dao).execute(chip)
    }
}