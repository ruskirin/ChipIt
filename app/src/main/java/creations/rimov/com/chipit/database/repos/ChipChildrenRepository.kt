package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import android.util.Log
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.ChipChildrenDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity

class ChipChildrenRepository(chipDb: ChipDatabase,
                             private val webComms: RepoChipRetriever) {

    private val chipAndChildrenDao = chipDb.chipChildrenDao()

    /**Get a ChipIdentity object and assign it as the parentChip in WebFragment
     * @param chipId: id of the object
     * @param useParent: true to use the parent of the chip whose id was passed, false to use the chip itself
     **/
    fun setParentIdentity(chipId: Long, useParent: Boolean) {
        AsyncGetChipIdentity(chipAndChildrenDao, webComms, useParent).execute(chipId)
    }

    fun getChipChildrenCardsLive(parentId: Long) = chipAndChildrenDao.getChipChildrenCardsLive(parentId)

    fun getChipChildrenCards(parentId: Long, type: Int) {

        AsyncGetChipCards(chipAndChildrenDao, webComms, type).execute(parentId)
    }

    fun insertChip(chip: Chip) {

        if(chip.parentId == -1L && !chip.isTopic) {
            Log.e("TopicChipRepo", "#insertChip: can't insert a chip that is not a topic and has parent id == -1L")
            return
        }

        AsyncInsertChip(chipAndChildrenDao).execute(chip)
    }

    fun deleteChip(chipId: Long) {

        AsyncGetChipToDelete(chipAndChildrenDao).execute(chipId)
    }

    /**
     * @param webHandler: interface to communicate with WebFragment
     * @param getParent: use chip's parent?
     */
    class AsyncGetChipIdentity(
        private val chipAndChildrenDao: ChipChildrenDao,
        private val webHandler: RepoChipRetriever,
        private val getParent: Boolean) : AsyncTask<Long, Void, ChipIdentity>() {

        override fun doInBackground(vararg params: Long?): ChipIdentity? {

            return chipAndChildrenDao.getChipIdentity(params[0] ?: return null)
        }

        override fun onPostExecute(result: ChipIdentity?) {

            if(result == null)
                return

            if(getParent)
                AsyncGetChipIdentity(chipAndChildrenDao, webHandler, false).execute(result.parentId)
            else
                webHandler.updateParent(result)
        }
    }

    class AsyncGetChipCards(
        private val chipAndChildrenDao: ChipChildrenDao,
        private val webHandler: RepoChipRetriever,
        private val type: Int) : AsyncTask<Long, Void, List<ChipCard>>() {

        override fun doInBackground(vararg params: Long?): List<ChipCard>? {

            return chipAndChildrenDao.getChipChildrenCards(params[0] ?: return null)
        }

        override fun onPostExecute(result: List<ChipCard>?) {

            if(result != null)
                webHandler.setChipList(result, type)
        }
    }

    class AsyncGetChipToDelete(private val chipAndChildrenDao: ChipChildrenDao) : AsyncTask<Long, Void, Chip>() {

        override fun doInBackground(vararg params: Long?): Chip? {

            return chipAndChildrenDao.getChip(params[0] ?: return null)
        }

        override fun onPostExecute(result: Chip?) {

            if(result == null)
                return

            AsyncDeleteChip(chipAndChildrenDao).execute(result)
        }
    }

    class AsyncDeleteChip(private val chipAndChildrenDao: ChipChildrenDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {
            chipAndChildrenDao.deleteChip(params[0])

            return null
        }
    }

    class AsyncInsertChip(private val chipAndChildrenDao: ChipChildrenDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {
            chipAndChildrenDao.insertChip(params[0])

            return null
        }
    }

    interface RepoChipRetriever {

        fun updateParent(parent: ChipIdentity)

        fun setChipList(chips: List<ChipCard>, type: Int)
    }
}