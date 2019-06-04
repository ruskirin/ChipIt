package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import android.util.Log
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.ChipChildrenDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity

class ChipChildrenRepository(chipDb: ChipDatabase,
                             private val webComms: RepoChipRetriever) : RepoHandler {

    private val chipAndChildrenDao = chipDb.chipChildrenDao()


    /**Recursive call from AsyncGetChipIdentity to use chip's parentId**/
    override fun getParent(chip: ChipIdentity) {
        setParentIdentity(chip.parentId, false)
    }

    /**Get a ChipIdentity object and assign it as the parentChip in WebFragment
     * @param chipId: id of the object
     * @param getParent: true to use the parent of the chip whose id was passed, false to use the chip itself
     **/
    fun setParentIdentity(chipId: Long, getParent: Boolean) {
        AsyncGetChipIdentity(chipAndChildrenDao, webComms, this, getParent).execute(chipId)
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

    /**
     * @param webHandler: interface to communicate with WebFragment
     * @param repoHandler: interface to communicate with ChipChildrenRepository
     * @param getParent: use chip's parent?
     */
    class AsyncGetChipIdentity(
        private val chipAndChildrenDao: ChipChildrenDao,
        private val webHandler: RepoChipRetriever,
        private val repoHandler: RepoHandler,
        private val getParent: Boolean) : AsyncTask<Long, Void, ChipIdentity>() {

        override fun doInBackground(vararg params: Long?): ChipIdentity? {

            if(params[0] == null)
                return null

            return chipAndChildrenDao.getChipIdentity(params[0]!!)
        }

        override fun onPostExecute(result: ChipIdentity?) {

            if(result == null)
                return

            if(getParent)
                repoHandler.getParent(result)
            else
                webHandler.updateParent(result)
        }
    }

    class AsyncGetChipCards(
        private val chipAndChildrenDao: ChipChildrenDao,
        private val webHandler: RepoChipRetriever,
        private val type: Int) : AsyncTask<Long, Void, List<ChipCard>>() {

        override fun doInBackground(vararg params: Long?): List<ChipCard>? {

            if(params[0] != null)
                return chipAndChildrenDao.getChipChildrenCards(params[0]!!)

            return null
        }

        override fun onPostExecute(result: List<ChipCard>?) {

            Log.i("Life Event", "AsyncGetChipCards#onPostExecute(): retrieved list size: ${result?.size ?: 0}")

            if(result != null)
                webHandler.setChipList(result, type)
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