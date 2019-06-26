package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import creations.rimov.com.chipit.database.daos.ChipDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipUpdateBasic

object DbAsyncTasks {

    class AsyncChipUpdate(private val dao: ChipDao) : AsyncTask<ChipUpdateBasic, Void, Void>() {

        override fun doInBackground(vararg params: ChipUpdateBasic): Void? {

            dao.updateChipBasic(params[0].id, params[0].name, params[0].desc, params[0].imgLocation)

            return null
        }
    }

    class InsertChip(private val dao: ChipDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {

            dao.insertChip(params[0])
            dao.increaseCounter(params[0].parentId ?: return null, params[0].counter+1)

            return null
        }
    }

    class DeleteChipTree(private val dao: ChipDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {

            //TODO FUTURE: if you need to delete the stored, linked material as well then that has to be incorporated
            dao.deleteChipTree(params[0].id)
            dao.decreaseCounter(params[0].parentId ?: return null, params[0].counter+1)

            return null
        }
    }
}