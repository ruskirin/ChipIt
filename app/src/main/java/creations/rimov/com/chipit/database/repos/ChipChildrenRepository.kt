package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.ChipChildrenDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard

class ChipChildrenRepository(chipDb: ChipDatabase) {

    private val chipAndChildrenDao = chipDb.chipChildrenDao()

    fun getChipChildrenCards(parentId: Long): LiveData<List<ChipCard>> = chipAndChildrenDao.getChipChildrenCards(parentId)

    fun insertChip(chip: Chip) {

        if(chip.parentId == 0L && !chip.isTopic) {
            Log.e("TopicChipRepo", "#insertChip: can't insert a chip that !is_topic and has parentId == 0L")
            return
        }

        AsyncChipInsert(chipAndChildrenDao).execute(chip)
    }

    class AsyncChipInsert(private val chipAndChildrenDao: ChipChildrenDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {
            chipAndChildrenDao.insertChip(params[0])

            return null
        }
    }
}