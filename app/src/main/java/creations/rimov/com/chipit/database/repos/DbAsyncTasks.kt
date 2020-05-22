package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import creations.rimov.com.chipit.database.daos.ChipDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.extensions.getChipUpdateDate
import java.util.*
import kotlin.NoSuchElementException

object DbAsyncTasks {

    class AsyncGetChip (
      private val dao: ChipDao,
      private val handler: AsyncHandler) : AsyncTask<Long, Void, Chip>() {

        override fun doInBackground(vararg params: Long?): Chip? {
            params[0]?.let {
                return dao.getChip(it)
            }

            return null
        }

        override fun onPostExecute(result: Chip?) {

            result?.let {
                handler.setData(it)
                return
            }

            throw NoSuchElementException("No chip found with provided id")
        }
    }

    class AsyncUpdateChip(private val dao: ChipDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {

            dao.updateChipBasic(params[0].id, params[0].name, params[0].desc, params[0].matPath)

            return null
        }
    }

    class AsyncInsertChip(private val dao: ChipDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {

            val date = Date().getChipUpdateDate()

            dao.insertChip(params[0])
            dao.increaseCounter(params[0].parentId ?: return null, params[0].numChildren + 1, date)

            return null
        }
    }

    class AsyncDeleteChipTree(private val dao: ChipDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {

            val date = Date().getChipUpdateDate()

            //TODO FUTURE: if you need to delete the stored, linked material as well then that has to be incorporated
            dao.deleteChipTree(params[0].id)
            dao.decreaseCounter(params[0].parentId ?: return null, params[0].numChildren + 1, date)

            return null
        }
    }
}