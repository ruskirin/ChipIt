package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import android.util.Log
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.ChipChildrenDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.objects.CoordPoint

class ChipRepository(chipDb: ChipDatabase,
                     private val chipHandler: ChipRepoHandler) {

    private val chipDao = chipDb.chipChildrenDao()

    fun getChipIdentity(id: Long) = chipDao.getChipIdentityLive(id)

    fun getChildrenPaths(parentId: Long) = chipDao.getChipChildrenPaths(parentId)

    fun isChipTopic(parentId: Long) {
        AsyncGetIsChipTopic(chipDao, chipHandler).execute(parentId)
    }

    fun updateChip(id: Long,
                   name: String? = null,
                   imgLocation: String? = null,
                   vertices: List<CoordPoint>? = null) = AsyncChipUpdate(chipDao, name, imgLocation, vertices).execute(id)

    fun insertChip(chip: Chip) = AsyncChipInsert(chipDao).execute(chip)


    class AsyncGetIsChipTopic(
        private val chipDao: ChipChildrenDao,
        private val chipHandler: ChipRepoHandler) : AsyncTask<Long, Void, Boolean>() {

        override fun doInBackground(vararg params: Long?): Boolean? {

            return chipDao.isChipTopic(params[0] ?: return null)
        }

        override fun onPostExecute(result: Boolean?) {

            if(result == null)
                return

            Log.i("Touch Event", "AsyncGetIsChipTopic#onPostExecute(): is parent topic? $result")
            chipHandler.isParentTopic(result)
        }
    }

    class AsyncChipUpdate(private val chipDao: ChipChildrenDao,
                          private val name: String?,
                          private val imgLocation: String?,
                          private val vertices: List<CoordPoint>?)
        : AsyncTask<Long, Void, Void>() {

        override fun doInBackground(vararg params: Long?): Void? {

            if(!name.isNullOrBlank()) chipDao.updateDescription(params[0]!!, name)
            if(!imgLocation.isNullOrBlank()) chipDao.updateImage(params[0]!!, imgLocation)
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

    interface ChipRepoHandler {

        fun isParentTopic(isTopic: Boolean)
    }
}
