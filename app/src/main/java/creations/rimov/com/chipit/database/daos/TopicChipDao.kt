package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.TopicAndChips

@Dao
interface TopicChipDao {

    @Transaction
    @Query("SELECT * FROM chips WHERE parent_id = :topicId")
    fun getTopicChips(topicId: Long): LiveData<List<Chip>>

    @Transaction
    @Query("SELECT id, name, image_location FROM chips WHERE parent_id = :topicId")
    fun getTopicChipCards(topicId: Long): LiveData<List<ChipCard>>
}