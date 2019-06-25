package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.ChipDao
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity

class AccessRepo(chipDb: ChipDatabase, private val accessRepoHandler: RepoHandler) {

    private val dao = chipDb.chipDao()

    fun getChipTopics() = dao.getChipTopicsLive()

    /**Get a ChipIdentity object and assign it as the parentChip in WebFragment
     * @param chipId: id of the object
     * @param useParent: true to use the parent of the chip whose id was passed, false to use the chip itself
     **/
    fun setParentIdentity(chipId: Long, useParent: Boolean) {
        AsyncGetChipIdentity(dao, accessRepoHandler, useParent).execute(chipId)
    }

    fun getChipIdentity(id: Long) = dao.getChipIdentity(id)

    fun getChipIdentityLive(id: Long) = dao.getChipIdentityLive(id)

    fun getChipCards(id: Long) {
        AsyncGetChipCards(dao, accessRepoHandler).execute(id)
    }

    fun getChipChildrenCardsLive(parentId: Long) = dao.getChipCardsLive(parentId)

    fun getChipPathsLive(parentId: Long) = dao.getChipPathsLive(parentId)


    class AsyncGetChipCards(
        private val chipDao: ChipDao,
        private val repoHandler: RepoHandler) : AsyncTask<Long, Void, List<ChipCard>>() {

        override fun doInBackground(vararg params: Long?): List<ChipCard>? {

            return chipDao.getChipCards(params[0] ?: return null)
        }

        override fun onPostExecute(result: List<ChipCard>?) {

            repoHandler.setData(result)
        }
    }

    /**
     * @param repoHandler: interface to communicate with WebFragment
     * @param getParent: use chip's parent?
     */
    class AsyncGetChipIdentity(
        private val chipDao: ChipDao,
        private val repoHandler: RepoHandler,
        private val getParent: Boolean) : AsyncTask<Long, Void, ChipIdentity>() {

        override fun doInBackground(vararg params: Long?): ChipIdentity? {

            return chipDao.getChipIdentity(params[0] ?: return null)
        }

        override fun onPostExecute(result: ChipIdentity?) {

            if(result == null)
                return

            if(getParent)
                AsyncGetChipIdentity(chipDao, repoHandler, false).execute(result.parentId)
            else
                repoHandler.setData(result)
        }
    }

    interface RepoHandler {

        fun <T> setData(data: T)
    }
}