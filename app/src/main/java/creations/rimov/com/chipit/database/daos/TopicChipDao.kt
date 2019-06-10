package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard

@Dao
interface TopicChipDao : BaseChipDao {

    @Query("SELECT * FROM chips WHERE id = :id AND is_topic")
    fun getTopicChip(id: Long): LiveData<Chip>

    @Query("SELECT id, parent_id, description, image_location FROM chips WHERE is_topic")
    fun getTopicChipCards(): LiveData<List<ChipCard>>
}