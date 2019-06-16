package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import android.util.Log
import creations.rimov.com.chipit.database.ChipDatabase
import creations.rimov.com.chipit.database.daos.ChipChildrenDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipIdentity

class AlbumRepository(chipDb: ChipDatabase,
                      private val webHandler: WebRepoHandler) {

    private val chipAndChildrenDao = chipDb.chipChildrenDao()

    /**Get a ChipIdentity object and assign it as the parentChip in AlbumFragment
     * @param chipId: id of the object
     * @param useParent: true to use the parent of the chip whose id was passed, false to use the chip itself
     **/
    fun setParentIdentity(chipId: Long, useParent: Boolean) {
        AsyncGetChipIdentity(chipAndChildrenDao, webHandler, useParent).execute(chipId)
    }

    fun getChipIdentity(chipId: Long) = chipAndChildrenDao.getChipIdentity(chipId)

    fun getChipChildrenCardsLive(parentId: Long) = chipAndChildrenDao.getChipChildrenCardsLive(parentId)

    fun insertChip(chip: Chip) {

        if(chip.parentId == -1L && !chip.isTopic) {
            Log.e("TopicChipRepo", "#insertChip: can't insert a chip that is not a topic and has parent id == -1L")
            return
        }

        DbAsyncTasks.InsertChip(chipAndChildrenDao).execute(chip)
    }

    fun deleteChipAndChildren(chipId: Long) {
        DbAsyncTasks.DeleteChipAndChildren(chipAndChildrenDao).execute(chipId)
    }

    /**
     * @param webHandler: interface to communicate with AlbumFragment
     * @param getParent: use chip's parent?
     */
    class AsyncGetChipIdentity(
        private val chipAndChildrenDao: ChipChildrenDao,
        private val webHandler: WebRepoHandler,
        private val getParent: Boolean) : AsyncTask<Long, Void, ChipIdentity>() {

        override fun doInBackground(vararg params: Long?): ChipIdentity? {

            return chipAndChildrenDao.getChipIdentity(params[0] ?: return null)
        }

        override fun onPostExecute(result: ChipIdentity?) {

            if(result == null)
                return

            if(getParent)
                AsyncGetChipIdentity(chipAndChildrenDao, webHandler, false).execute(result.parentId)
            else
                webHandler.updateParent(result)
        }
    }

    interface WebRepoHandler {

        fun updateParent(parent: ChipIdentity)
    }
}