package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.TopicChipDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipTopic
import creations.rimov.com.chipit.database.objects.TopicAndChildren

class DirectoryRepository(chipDb: ChipDatabase, private val dirHandler: DirRepoHandler) {

    private val topicDao = chipDb.topicDao()

    fun setTopics() {
        AsyncGetTopics(topicDao, dirHandler).execute()
    }

    fun update(id: Long, name: String?, imgLocation: String?) {

        AsyncChipUpdate(topicDao, name, imgLocation).execute(id)
    }

    fun insert(topic: Chip) {
        DbAsyncTasks.InsertChip(topicDao).execute(topic)
    }

    fun deleteTopicAndChildren(parentId: Long) {
        DbAsyncTasks.DeleteChipAndChildren(topicDao).execute(parentId)
    }


    class AsyncGetTopics(private val topicDao: TopicChipDao,
                         private val dirHandler: DirRepoHandler) : AsyncTask<Void, Void, List<ChipTopic>>() {

        override fun doInBackground(vararg params: Void?): List<ChipTopic> {

            return topicDao.getChipTopics()
        }

        override fun onPostExecute(result: List<ChipTopic>) {

            AsyncGetTopicChildren(topicDao, dirHandler).execute(result)
        }
    }

    class AsyncGetTopicChildren(private val topicDao: TopicChipDao,
                                private val dirHandler: DirRepoHandler) : AsyncTask<List<ChipTopic>, Void, List<TopicAndChildren>>() {

        override fun doInBackground(vararg params: List<ChipTopic>): List<TopicAndChildren> {

            val topicAndChildren = mutableListOf<TopicAndChildren>()

            params[0].forEach {
                topicAndChildren.add(TopicAndChildren(it, topicDao.getChipCards(it.id)))
            }

            return topicAndChildren
        }

        override fun onPostExecute(result: List<TopicAndChildren>) {

            dirHandler.setTopics(result)
        }
    }

    class AsyncChipUpdate(private val topicDao: TopicChipDao,
                          private val name: String?,
                          private val imgLocation: String?) : AsyncTask<Long, Void, Void>() {

        override fun doInBackground(vararg params: Long?): Void? {

            if(!name.isNullOrBlank()) topicDao.updateDescription(params[0]!!, name)
            if(!imgLocation.isNullOrBlank()) topicDao.updateImage(params[0]!!, imgLocation)

            return null
        }
    }

    interface DirRepoHandler {

        fun setTopics(topics: List<TopicAndChildren>)
    }
}