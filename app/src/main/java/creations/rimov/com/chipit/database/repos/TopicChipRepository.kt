package creations.rimov.com.chipit.database.repos

import androidx.lifecycle.LiveData
import creations.rimov.com.chipit.database.TopicDatabase
import creations.rimov.com.chipit.database.objects.ChipAndChildren
import creations.rimov.com.chipit.database.objects.TopicAndChips

class TopicChipRepository(topicDb: TopicDatabase) {

    private val topicChipsDao = topicDb.topicChipsDao()
    private val chipChildrenDao = topicDb.chipChildrenDao()

    private lateinit var topicAndChips: LiveData<TopicAndChips>
    private lateinit var chipAndChildren: LiveData<ChipAndChildren>

    fun getTopicChips(topicId: Long = 0L): LiveData<TopicAndChips> {

        if(topicId != 0L)
            topicAndChips = topicChipsDao.getTopicChips(topicId)

        return topicAndChips
    }

    fun getChipChildren(parentId: Long = 0L): LiveData<ChipAndChildren> {

        if(parentId != 0L)
            chipAndChildren = chipChildrenDao.getAllChildren(parentId)

        return chipAndChildren
    }
}