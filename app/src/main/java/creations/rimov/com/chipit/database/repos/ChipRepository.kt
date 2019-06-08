package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.ChipChildrenDao
import creations.rimov.com.chipit.database.daos.ChipDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.objects.CoordPoint

class ChipRepository(chipDb: ChipDatabase,
                     private val chipComm: ChipRepoCommunication) {

    private val chipDao = chipDb.chipDao()
    private val chipChildrenDao = chipDb.chipChildrenDao()

    fun setParentIdentity(id: Long) {
        AsyncGetParentIdentity(chipChildrenDao, chipComm).execute(id)
    }

    fun isChipTopic(parentId: Long) {
        AsyncGetIsChipTopic(chipDao, chipComm).execute(parentId)
    }

    fun getChildren(parentId: Long) = chipChildrenDao.getChipChildrenPaths(parentId)

    fun updateChip(id: Long,
                   name: String? = null,
                   imgLocation: String? = null,
                   vertices: List<CoordPoint>? = null) = AsyncChipUpdate(chipDao, name, imgLocation, vertices).execute(id)

    fun insertChip(chip: Chip) = AsyncChipInsert(chipChildrenDao, chipComm).execute(chip)


    /**
     * @param chipHandler: interface to communicate with WebFragment
     * @param getParent: use chip's parent?
     */
    class AsyncGetParentIdentity(
        private val chipChildrenDao: ChipChildrenDao,
        private val chipHandler: ChipRepoCommunication) : AsyncTask<Long, Void, ChipIdentity>() {

        override fun doInBackground(vararg params: Long?): ChipIdentity? {

            return chipChildrenDao.getChipIdentity(params[0] ?: return null)
        }

        override fun onPostExecute(result: ChipIdentity?) {

            if(result == null)
                return

            chipHandler.updateParent(result)
        }
    }

    class AsyncGetIsChipTopic(
        private val chipDao: ChipDao,
        private val chipHandler: ChipRepoCommunication) : AsyncTask<Long, Void, Boolean>() {

        override fun doInBackground(vararg params: Long?): Boolean? {

            return chipDao.isParentTopic(params[0] ?: return null)
        }

        override fun onPostExecute(result: Boolean?) {

            if(result == null)
                return

            chipHandler.isParentTopic(result)
        }
    }

    class AsyncChipUpdate(private val chipDao: ChipDao,
                          private val name: String?,
                          private val imgLocation: String?,
                          private val vertices: List<CoordPoint>?)
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
            chipComm.setChipId(id)

            return null
        }
    }

    interface ChipRepoCommunication {

        fun setChipId(id: Long)

        fun updateParent(parent: ChipIdentity)

        fun isParentTopic(isTopic: Boolean)
    }
}
