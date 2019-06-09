package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import creations.rimov.com.chipit.database.objects.Chip

@Dao
interface TopicChipDao : BaseChipDao {

    @Query("SELECT * FROM chips WHERE id = :id AND is_topic")
    fun getTopicChip(id: Long): LiveData<Chip>

    @Query("SELECT * FROM chips WHERE is_topic")
    fun getTopicChips(): LiveData<List<Chip>>
}