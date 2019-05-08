package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import creations.rimov.com.chipit.database.TopicDatabase
import creations.rimov.com.chipit.database.daos.TopicDao
import creations.rimov.com.chipit.database.objects.Topic

class TopicRepository(topicDb: TopicDatabase) {

    private val topicDao = topicDb.topicDao()

    fun getAll() = topicDao.getAll()

    fun getOne(id: Long) = topicDao.getOne(id)

    fun insert(topic: Topic) = AsyncTopicInsert(topicDao).execute(topic)

    fun update(id: Long, name: String = "", imgLocation: String = "")
            = AsyncTopicUpdate(topicDao, id, name, imgLocation).execute()

    fun delete(topic: Topic) = AsyncTopicDelete(topicDao).execute(topic)


    class AsyncTopicDelete(private val topicDao: TopicDao) : AsyncTask<Topic, Void, Void>() {

        override fun doInBackground(vararg params: Topic): Void? {
            topicDao.deleteTopic(params[0])

            return null
        }
    }

    class AsyncTopicUpdate(private val topicDao: TopicDao,
                           private val id: Long,
                           private val name: String = "",
                           private val imgLocation: String = "") : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void): Void? {

            if(name.isNotBlank()) topicDao.updateName(id, name)
            if(imgLocation.isNotBlank()) topicDao.updateImage(id, imgLocation)

            return null
        }
    }

    class AsyncTopicInsert(private val topicDao: TopicDao) : AsyncTask<Topic, Void, Void>() {

        override fun doInBackground(vararg params: Topic): Void? {
            topicDao.insertTopic(params[0])

            return null
        }
    }
}