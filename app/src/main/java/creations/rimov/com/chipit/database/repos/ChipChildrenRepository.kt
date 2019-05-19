package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import android.util.Log
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.ChipChildrenDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard

class ChipChildrenRepository(chipDb: ChipDatabase,
                             private val chipComm: RepoChipRetriever) {

    private val chipAndChildrenDao = chipDb.chipChildrenDao()

    fun getChipChildrenCards(parentId: Long, type: Int) {

        AsyncChipGet(chipAndChildrenDao, chipComm, type).execute(parentId)
    }

    fun insertChip(chip: Chip) {

        if(chip.parentId == 0L && !chip.isTopic) {
            Log.e("TopicChipRepo", "#insertChip: can't insert a chip that !is_topic and has parentId == 0L")
            return
        }

        AsyncChipInsert(chipAndChildrenDao).execute(chip)
    }

    class AsyncChipGet(
        private val chipAndChildrenDao: ChipChildrenDao,
        private val chipComm: RepoChipRetriever,
        private val type: Int) : AsyncTask<Long, Void, Void>() {

        override fun doInBackground(vararg params: Long?): Void? {

            if(params[0] != null)
                chipComm.setChipList(chipAndChildrenDao.getChipChildrenCardsTwo(params[0]!!), type)

            return null
        }
    }

    class AsyncChipInsert(private val chipAndChildrenDao: ChipChildrenDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {
            chipAndChildrenDao.insertChip(params[0])

            return null
        }
    }

    interface RepoChipRetriever {

        fun setChipList(chips: List<ChipCard>, type: Int)
    }
}