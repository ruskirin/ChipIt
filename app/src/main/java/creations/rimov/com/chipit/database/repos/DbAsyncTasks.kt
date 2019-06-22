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

    class DeleteChipTree(private val dao: ChipDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {

            //TODO FUTURE: if you need to delete the stored, linked material as well then that has to be incorporated
            dao.deleteChipTree(params[0].id)
            dao.decreaseCounter(params[0].parentId ?: return null, params[0].counter+1)

            return null
        }
    }
}