package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.TopicChipDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.util.CameraUtil

class DirectoryRepository(chipDb: ChipDatabase) {

    private val topicDao = chipDb.topicDao()


    fun getTopic(id: Long) = topicDao.getTopicChip(id)

    fun getTopics() = topicDao.getTopicChips()

    fun update(id: Long,
                   name: String?,
                   imgLocation: String?) = AsyncChipUpdate(topicDao, name, imgLocation).execute(id)

    fun insert(topic: Chip) = DbAsyncTasks.InsertChip(topicDao).execute(topic)

    fun delete(chips: List<Chip>) {

    }


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

    class AsyncDeleteChip(private val topicDao: TopicChipDao) : AsyncTask<Long, Void, Void>() {

        override fun doInBackground(vararg params: Long?): Void? {

            val chip = topicDao.getChip(params[0] ?: return null)

            topicDao.deleteChip(chip)

            CameraUtil.deleteImageFile(chip.imgLocation)

            return null
        }
    }
}