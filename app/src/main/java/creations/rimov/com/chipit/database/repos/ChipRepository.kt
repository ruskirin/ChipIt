package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.ChipChildrenDao
import creations.rimov.com.chipit.database.daos.ChipDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.objects.Point

class ChipRepository(chipDb: ChipDatabase,
                     private val chipComm: ChipRepoCommunication) {

    private val chipDao = chipDb.chipDao()
    private val chipChildrenDao = chipDb.chipChildrenDao()

    fun getChip(id: Long) = chipDao.getChip(id)

    fun getChildren(parentId: Long) = chipChildrenDao.getChipChildren(parentId)

    fun updateChip(id: Long,
                   name: String?,
                   imgLocation: String?,
                   vertices: List<Point>?) = AsyncChipUpdate(chipDao, name, imgLocation, vertices).execute(id)

    fun insertChip(chip: Chip) = AsyncChipInsert(chipChildrenDao, chipComm).execute(chip)


    class AsyncChipUpdate(private val chipDao: ChipDao,
                          private val name: String?,
                          private val imgLocation: String?,
                          private val vertices: List<Point>?)
        : AsyncTask<Long, Void, Void>() {

        override fun doInBackground(vararg params: Long?): Void? {

            if(!name.isNullOrBlank()) chipDao.updateName(params[0]!!, name)
            if(!imgLocation.isNullOrBlank()) chipDao.updateImage(params[0]!!, imgLocation)
            if(!vertices.isNullOrEmpty()) chipDao.updateVertices(params[0]!!, vertices)

            return null
        }
    }

    class AsyncChipInsert(private val chipChildrenDao: ChipChildrenDao,
                          private val chipComm: ChipRepoCommunication) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {
            val id = chipChildrenDao.insertChip(params[0])
            //Return the newly-inserted chip id
            chipComm.returnChipId(id)

            return null
        }
    }

    interface ChipRepoCommunication {

        fun returnChipId(id: Long)
    }
}
