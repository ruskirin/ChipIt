package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.TopicChipDao
import creations.rimov.com.chipit.database.objects.Chip

class TopicChipRepository(chipDb: ChipDatabase) {

    private val topicDao = chipDb.topicDao()


    fun getTopic(id: Long) = topicDao.getTopicChip(id)

    fun getTopics() = topicDao.getTopicChips()

    fun update(id: Long,
                   name: String?,
                   imgLocation: String?) = AsyncChipUpdate(topicDao, name, imgLocation).execute(id)

    fun insert(topic: Chip) = AsyncChipInsert(topicDao).execute(topic)


    class AsyncChipUpdate(private val topicDao: TopicChipDao,
                          private val name: String?,
                          private val imgLocation: String?)
        : AsyncTask<Long, Void, Void>() {

        override fun doInBackground(vararg params: Long?): Void? {

            if(!name.isNullOrBlank()) topicDao.updateName(params[0]!!, name)
            if(!imgLocation.isNullOrBlank()) topicDao.updateImage(params[0]!!, imgLocation)

            return null
        }
    }

    class AsyncChipInsert(private val topicDao: TopicChipDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {
            topicDao.insertTopic(params[0])

            return null
        }
    }
}