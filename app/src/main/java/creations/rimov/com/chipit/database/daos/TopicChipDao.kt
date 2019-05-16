package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import creations.rimov.com.chipit.database.objects.Chip

@Dao
interface TopicChipDao {

    @Query("SELECT * FROM chips WHERE id = :id AND is_topic")
    fun getTopicChip(id: Long): LiveData<Chip>

    @Query("SELECT * FROM chips WHERE is_topic")
    fun getTopicChips(): LiveData<List<Chip>>

    @Query("UPDATE chips SET name = :name WHERE id = :id")
    fun updateName(id: Long, name: String): Int

    @Query("UPDATE chips SET image_location = :imgLocation WHERE id = :id")
    fun updateImage(id: Long, imgLocation: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTopic(topic: Chip): Long

    @Delete
    fun deleteTopic(vararg topic: Chip): Int
}