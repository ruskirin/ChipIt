package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import creations.rimov.com.chipit.database.daos.ChipDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.objects.ChipUpdateBasic
import creations.rimov.com.chipit.extensions.getChipUpdateDate
import java.util.*

object DbAsyncTasks {

    class AsyncChipUpdate(private val dao: ChipDao) : AsyncTask<ChipUpdateBasic, Void, Void>() {

        override fun doInBackground(vararg params: ChipUpdateBasic): Void? {

            dao.updateChipBasic(params[0].id, params[0].name, params[0].desc, params[0].repPath)

            return null
        }
    }

    class InsertChip(private val dao: ChipDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {

            val date = Date().getChipUpdateDate()

            dao.insertChip(params[0])
            dao.increaseCounter(params[0].parentId ?: return null, params[0].counter+1, date)

            return null
        }
    }

    class DeleteChipTree(private val dao: ChipDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {

            val date = Date().getChipUpdateDate()

            //TODO FUTURE: if you need to delete the stored, linked material as well then that has to be incorporated
            dao.deleteChipTree(params[0].id)
            dao.decreaseCounter(params[0].parentId ?: return null, params[0].counter+1, date)

            return null
        }
    }
}