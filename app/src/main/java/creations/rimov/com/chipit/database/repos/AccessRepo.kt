package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.AccessDao
import creations.rimov.com.chipit.database.daos.EditDao
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.objects.ChipTopic
import creations.rimov.com.chipit.database.objects.TopicAndChildren
import creations.rimov.com.chipit.objects.CoordPoint

class AccessRepo(chipDb: ChipDatabase, private val accessRepoHandler: RepoHandler) {

    private val dao = chipDb.accessDao()

    fun setTopics() {
        AsyncGetTopics(dao, accessRepoHandler).execute()
    }

    /**Get a ChipIdentity object and assign it as the parentChip in AlbumFragment
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
     * @param repoHandler: interface to communicate with AlbumFragment
     * @param getParent: use chip's parent?
     */
    class AsyncGetChipIdentity(
        private val chipAndChildrenDao: AccessDao,
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


    class AsyncGetTopics(private val dao: AccessDao,
                         private val repoHandler: RepoHandler
    ) : AsyncTask<Void, Void, List<ChipTopic>>() {

        override fun doInBackground(vararg params: Void?): List<ChipTopic> {

            return dao.getChipTopics()
        }

        override fun onPostExecute(result: List<ChipTopic>) {

            AsyncGetTopicChildren(dao, repoHandler).execute(result)
        }
    }

    class AsyncGetTopicChildren(private val dao: AccessDao,
                                private val repoHandler: RepoHandler
    ) : AsyncTask<List<ChipTopic>, Void, List<TopicAndChildren>>() {

        override fun doInBackground(vararg params: List<ChipTopic>): List<TopicAndChildren> {

            val topicAndChildren = mutableListOf<TopicAndChildren>()

            params[0].forEach {
                topicAndChildren.add(TopicAndChildren(it, dao.getChipCards(it.id)))
            }

            return topicAndChildren
        }

        override fun onPostExecute(result: List<TopicAndChildren>) {

            repoHandler.setData(result)
        }
    }

    class AsyncChipUpdate(private val dao: EditDao,
                          private val name: String?,
                          private val imgLocation: String?,
                          private val vertices: List<CoordPoint>?)
        : AsyncTask<Long, Void, Void>() {

        override fun doInBackground(vararg params: Long?): Void? {

            if(!name.isNullOrBlank()) dao.updateDescription(params[0]!!, name)
            if(!imgLocation.isNullOrBlank()) dao.updateImage(params[0]!!, imgLocation)
            if(!vertices.isNullOrEmpty()) dao.updateVertices(params[0]!!, vertices)

            return null
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