package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.ChipDao
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

    fun getChipChildrenCardsLive(parentId: Long) = dao.getChipCardsLive(parentId)

    fun getChipPathsLive(parentId: Long) = dao.getChipPathsLive(parentId)



    /**
     * @param repoHandler: interface to communicate with WebFragment
     * @param getParent: use chip's parent?
     */
    class AsyncGetChipIdentity(
        private val chipAndChildrenDao: ChipDao,
        private val repoHandler: RepoHandler,
        private val getParent: Boolean) : AsyncTask<Long, Void, ChipIdentity>() {

        override fun doInBackground(vararg params: Long?): ChipIdentity? {

            return chipAndChildrenDao.getChipIdentity(params[0] ?: return null)
        }

        override fun onPostExecute(result: ChipIdentity?) {

            if(result == null)
                return

            if(getParent)
                AsyncGetChipIdentity(chipAndChildrenDao, repoHandler, false).execute(result.parentId)
            else
                repoHandler.setData(result)
        }
    }

//    class AsyncGetIsChipTopic(
//        private val chipDao: ChipChildrenDao,
//        private val chipHandler: WebRepository.ChipRepoHandler
//    ) : AsyncTask<Long, Void, Boolean>() {
//
//        override fun doInBackground(vararg params: Long?): Boolean? {
//
//            return chipDao.isChipTopic(params[0] ?: return null)
//        }
//
//        override fun onPostExecute(result: Boolean?) {
//
//            if(result == null)
//                return
//
//            Log.i("Touch Event", "AsyncGetIsChipTopic#onPostExecute(): is parent name? $result")
//            chipHandler.isParentTopic(result)
//        }
//    }

    interface RepoHandler {

        fun <T> setData(data: T)
    }
}