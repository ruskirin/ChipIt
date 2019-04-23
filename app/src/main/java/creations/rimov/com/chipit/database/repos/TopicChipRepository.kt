package creations.rimov.com.chipit.database.repos

import androidx.lifecycle.LiveData
import creations.rimov.com.chipit.database.TopicDatabase
import creations.rimov.com.chipit.database.objects.ChipAndChildren
import creations.rimov.com.chipit.database.objects.TopicAndChips

class TopicChipRepository(topicDb: TopicDatabase) {

    private val topicChipsDao = topicDb.topicChipsDao()
    private val chipChildrenDao = topicDb.chipChildrenDao()

    fun getTopicAndChips(topicId: Long): TopicAndChips = topicChipsDao.getTopicChips(topicId)

    fun getChipAndChildren(parentId: Long): ChipAndChildren = chipChildrenDao.getAllChildren(parentId)
}