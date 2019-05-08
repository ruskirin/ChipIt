package creations.rimov.com.chipit.database.repos

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import creations.rimov.com.chipit.database.TopicDatabase
import creations.rimov.com.chipit.database.daos.ChipChildrenDao
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard

class TopicChipRepository(topicDb: TopicDatabase) {

    private val topicAndChipsDao = topicDb.topicChipDao()
    private val chipAndChildrenDao = topicDb.chipChildrenDao()

    fun getTopicChipCards(topicId: Long): LiveData<List<ChipCard>> = topicAndChipsDao.getTopicChipCards(topicId)
    fun getChipChildrenCards(parentId: Long): LiveData<List<ChipCard>> = chipAndChildrenDao.getChipChildrenCards(parentId)

    fun insertChip(chip: Chip) {
        //TODO: no parentId specified, throw some kind of warning
        if(chip.parentId != 0L)
            AsyncChipInsert(chipAndChildrenDao).execute(chip)
        else
            Log.e("TopicChipRepo", "#insertChip: chip has no parent_id")
    }

    class AsyncChipInsert(private val chipAndChildrenDao: ChipChildrenDao) : AsyncTask<Chip, Void, Void>() {

        override fun doInBackground(vararg params: Chip): Void? {
            chipAndChildrenDao.insertChip(params[0])

            return null
        }
    }
}