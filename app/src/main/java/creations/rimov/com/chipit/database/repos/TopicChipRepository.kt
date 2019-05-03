package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import creations.rimov.com.chipit.database.TopicDatabase
import creations.rimov.com.chipit.database.daos.ChipChildrenDao
import creations.rimov.com.chipit.database.objects.Chip

class TopicChipRepository(topicDb: TopicDatabase) {

    private val topicAndChipsDao = topicDb.topicChipDao()
    private val chipAndChildrenDao = topicDb.chipChildrenDao()

    fun getTopicChips(topicId: Long): LiveData<List<Chip>> = topicAndChipsDao.getTopicAndChips(topicId)
    fun getChipAndChildren(parentId: Long): LiveData<List<Chip>> = chipAndChildrenDao.getAllChildren(parentId)

    fun insertChild(child: Chip) {
        //TODO: no parentId specified, throw some kind of warning
        if(child.parentId != 0L)
            AsyncChipInsert(chipAndChildrenDao).execute(child)
        else
            Log.e("TopicChipRepo", "#insertChild: child has no parent_id")
    }

    class AsyncChipInsert(private val chipAndChildrenDao: ChipChildrenDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip?): Void? {
            if(params[0] != null)
                chipAndChildrenDao.insertChip(params[0]!!)

            return null
        }
    }
}