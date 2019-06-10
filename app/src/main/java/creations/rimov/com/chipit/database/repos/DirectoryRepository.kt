package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.TopicChipDao
import creations.rimov.com.chipit.database.objects.Chip

class DirectoryRepository(chipDb: ChipDatabase) {

    private val topicDao = chipDb.topicDao()

    fun getTopic(id: Long) = topicDao.getTopicChip(id)

    fun getTopics() = topicDao.getTopicChipCards()

    fun update(id: Long,
                   name: String?,
                   imgLocation: String?) = AsyncChipUpdate(topicDao, name, imgLocation).execute(id)

    fun insert(topic: Chip) {
        DbAsyncTasks.InsertChip(topicDao).execute(topic)
    }

    fun deleteTopicAndChildren(parentId: Long) {
        DbAsyncTasks.DeleteChipAndChildren(topicDao).execute(parentId)
    }


    class AsyncChipUpdate(private val topicDao: TopicChipDao,
                          private val name: String?,
                          private val imgLocation: String?)
        : AsyncTask<Long, Void, Void>() {

        override fun doInBackground(vararg params: Long?): Void? {

            if(!name.isNullOrBlank()) topicDao.updateDescription(params[0]!!, name)
            if(!imgLocation.isNullOrBlank()) topicDao.updateImage(params[0]!!, imgLocation)

            return null
        }
    }
}