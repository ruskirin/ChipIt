package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import creations.rimov.com.chipit.database.daos.BaseChipDao
import creations.rimov.com.chipit.database.objects.Chip

object DbAsyncTasks {

    class InsertChip(private val dao: BaseChipDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {
            dao.insertChip(params[0])

            return null
        }
    }

    /** Retrieve all the children of the passed chip, going down the "family tree"
     * @param delete: should the retrieved children be deleted?
     * @param handler: if !delete, use this base interface to communicate results back to calling activity
     */
    class GetChildrenIdsFull(private val dao: BaseChipDao,
                             private val delete: Boolean,
                             private val handler: BaseAsyncTaskHandler<Long>? = null) : AsyncTask<Long, Void, List<Long>?>() {

        override fun doInBackground(vararg params: Long?): List<Long>? {

            //Nothing passed OR marked as !delete but no handler was passed to communicate results back
            if((params[0] == null) || (!delete && handler == null))
                return null

            val childrenIds: MutableList<Long> = mutableListOf(params[0]!!)

            childrenIds.forEach { id ->
                childrenIds.addAll(dao.getChildrenIds(id))
            }

            return childrenIds
        }

        override fun onPostExecute(result: List<Long>?) {

            if(result == null)
                return

            if(!delete) {
                handler!!.setResult(result)
                return
            }

            DeleteChipsById(dao).execute(result)
        }
    }

    //TODO FUTURE: this could be a long-running task, see if loading indication is needed
    class DeleteChipsById(private val dao: BaseChipDao) : AsyncTask<List<Long>, Void, Void>() {

        override fun doInBackground(vararg params: List<Long>): Void? {

            params[0].forEach { id ->
                dao.deleteChip(id)
            }

            return null
        }
    }

    interface BaseAsyncTaskHandler<T> {

        fun setResult(result: List<T>)
    }
}