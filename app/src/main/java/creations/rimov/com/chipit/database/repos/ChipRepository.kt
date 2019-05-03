package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import creations.rimov.com.chipit.database.TopicDatabase
import creations.rimov.com.chipit.database.daos.ChipChildrenDao
import creations.rimov.com.chipit.database.daos.ChipDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.objects.Point

class ChipRepository(topicDb: TopicDatabase) {

    private val chipDao = topicDb.chipDao()
    private val chipChildrenDao = topicDb.chipChildrenDao()

    fun getChip(id: Long) = chipDao.getChip(id)

    fun getChildren(parentId: Long) = chipChildrenDao.getAllChildren(parentId)

    fun updateChip(id: Long,
                   name: String?,
                   imagePath: String?,
                   vertices: List<Point>?) = AsyncChipUpdate(chipDao, name, imagePath, vertices).execute(id)

    fun insertChip(chip: Chip) = AsyncChipInsert(chipChildrenDao).execute(chip)


    class AsyncChipUpdate(private val chipDao: ChipDao,
                          private val name: String?,
                          private val imagePath: String?,
                          private val vertices: List<Point>?)
        : AsyncTask<Long, Void, Void>() {

        override fun doInBackground(vararg params: Long?): Void? {

            if(!name.isNullOrBlank()) chipDao.updateName(params[0]!!, name)
            if(!imagePath.isNullOrBlank()) chipDao.updateImage(params[0]!!, imagePath)
            if(!vertices.isNullOrEmpty()) chipDao.updateVertices(params[0]!!, vertices)

            return null
        }
    }

    class AsyncChipInsert(private val chipChildrenDao: ChipChildrenDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {
            chipChildrenDao.insertChip(params[0])

            return null
        }
    }
}
