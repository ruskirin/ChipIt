package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipTopic

@Dao
interface TopicChipDao : BaseChipDao {

    @Query("SELECT id, name, num_children, description, date_create, date_update FROM chips WHERE is_topic")
    fun getChipTopics(): List<ChipTopic>

    @Query("SELECT id, name, image_location FROM chips WHERE parent_id = :parentId")
    fun getChipCards(parentId: Long): List<ChipCard>
}