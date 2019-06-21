package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import android.util.Log
import creations.rimov.com.chipit.database.daos.EditDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.util.CameraUtil

object DbAsyncTasks {

    class AsyncChipUpdate(private val editDao: EditDao,
                          private val name: String?,
                          private val imgLocation: String?) : AsyncTask<Long, Void, Void>() {

        override fun doInBackground(vararg params: Long?): Void? {

            if(!name.isNullOrBlank()) editDao.updateDescription(params[0]!!, name)
            if(!imgLocation.isNullOrBlank()) editDao.updateImage(params[0]!!, imgLocation)

            return null
        }
    }

    class InsertChip(private val dao: EditDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {
            dao.insertChip(params[0])

            return null
        }
    }

    /**Retrieve all the children of the passed chip, going down the "family tree"**/
//    class DeleteChipAndChildren(private val dao: EditDao) : AsyncTask<Long, Void, List<Long>?>() {
//
//        override fun doInBackground(vararg params: Long?): List<Long>? {
//
//            if(params[0] == null)
//                return null
//
//            var index = 0
//            val childrenIds: MutableList<Long> = mutableListOf(params[0]!!)
//
//            //Get children of each index and add to end of childrenIds, whose children get called eventually as well till
//            // the last chip in the "family tree" is reached
//            while(index < childrenIds.size) {
//                val ids = dao.getChildrenIds(childrenIds[index])
//                ++index
//
//                if(ids.isNullOrEmpty())
//                    continue
//
//                childrenIds.addAll(ids)
//            }
//
//            Log.i("Touch Event", "DeleteChipAndChildren#doInBackground(): total ${childrenIds.size} to delete")
//
//            return childrenIds
//        }
//
//        override fun onPostExecute(result: List<Long>?) {
//
//            if(result == null)
//                return
//
//            DeleteChipsById(dao).execute(result)
//        }
//    }

    //TODO FUTURE: this could be a long-running task, see if loading indication is needed
    class DeleteChipsById(private val dao: EditDao) : AsyncTask<List<Long>, Void, Void>() {

        override fun doInBackground(vararg params: List<Long>): Void? {

            params[0].forEach { id ->
                CameraUtil.deleteImageFile(dao.getChipImage(id))
                dao.deleteChip(id)
            }

            return null
        }
    }
}