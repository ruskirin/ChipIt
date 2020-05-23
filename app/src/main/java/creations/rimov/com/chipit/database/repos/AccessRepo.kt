package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.ChipDao
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.objects.ChipReference

class AccessRepo(chipDb: ChipDatabase, private val handler: RepoHandler) {

    private val dao = chipDb.chipDao()

    fun getChipAsync(id: Long) {
        DbAsyncTasks.AsyncGetChip(dao, handler).execute(id)
    }

    fun getChipTopics() = dao.getChipTopicsLive()

    fun getChipReferenceParentTreeLive(id: Long?): LiveData<List<ChipReference>> = dao.getChipReferenceParentTreeLive(id)

    /**Get a ChipIdentity object and assign it as the parentChip in WebFragment
     * @param chipId: id of the object
     * @param useParent: true to use the parent of the chip whose id was passed, false to use the chip itself
     **/
    fun setWebParent(chipId: Long?) {
        AsyncGetChipIdentity(dao, handler).execute(chipId)
    }

    fun getChipTopics(id: Long) = dao.getChipIdentity(id)

    fun getChipIdentityLive(id: Long?) =
        if(id == null) null
        else dao.getChipIdentityLive(id)

    fun getChipCards(id: Long) {
        AsyncGetChipCards(dao, handler).execute(id)
    }

    fun getChipChildrenCardsLive(parentId: Long?): LiveData<List<ChipCard>> {

        if(parentId == null) return dao.getChipCardsOfNullLive()

        return dao.getChipCardsLive(parentId)
    }

    fun getChipPathsLive(parentId: Long?) = dao.getChipPathsLive(parentId)


    class AsyncGetChipCards(
        private val chipDao: ChipDao,
        private val repoHandler: RepoHandler) : AsyncTask<Long, Void, List<ChipCard>>() {

        override fun doInBackground(vararg params: Long?): List<ChipCard>? {
            return chipDao.getChipCards(params[0] ?: return null)
        }

        override fun onPostExecute(result: List<ChipCard>) {
            repoHandler.setDataList(result)
        }
    }

    class AsyncGetChipIdentity(
        private val chipDao: ChipDao,
        private val repoHandler: RepoHandler) : AsyncTask<Long?, Void, ChipIdentity>() {

        override fun doInBackground(vararg params: Long?): ChipIdentity? {
            return chipDao.getChipIdentity(params[0] ?: return null)
        }

        override fun onPostExecute(result: ChipIdentity?) {
            repoHandler.setData(result)
        }
    }
}