package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import creations.rimov.com.chipit.database.objects.TopicAndChips

@Dao
interface TopicChipDao : BaseDao {

    @Transaction
    @Query("SELECT * FROM topics WHERE id = :topicId")
    fun getTopicChips(topicId: Long): LiveData<TopicAndChips>
}