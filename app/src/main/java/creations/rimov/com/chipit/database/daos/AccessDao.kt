package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import creations.rimov.com.chipit.database.objects.*

@Dao
interface AccessDao {

    @Transaction
    @Query("SELECT * FROM chips WHERE parent_id = :parentId")
    fun getChipsLive(parentId: Long): LiveData<List<Chip>>

    @Transaction
    @Query("SELECT id, name, num_children, description, date_create, date_update FROM chips WHERE is_topic")
    fun getChipTopics(): List<ChipTopic>

    @Transaction
    @Query("SELECT id, name, num_children, description, date_create, date_update FROM chips WHERE is_topic")
    fun getChipTopicsLive(): LiveData<List<ChipTopic>>

    @Transaction
    @Query("SELECT id, name, image_location FROM chips WHERE parent_id = :parentId")
    fun getChipCards(parentId: Long): List<ChipCard>

    @Transaction
    @Query("SELECT id, name, image_location FROM chips WHERE parent_id = :parentId")
    fun getChipCardsLive(parentId: Long): LiveData<List<ChipCard>>

    @Query("SELECT id, parent_id, is_topic, description, image_location FROM chips WHERE id = :id")
    fun getChipIdentity(id: Long): ChipIdentity

    @Query("SELECT id, parent_id, is_topic, description, image_location FROM chips WHERE id = :id")
    fun getChipIdentityLive(id: Long): LiveData<ChipIdentity>

    @Transaction
    @Query("SELECT id, image_location, vertices FROM chips WHERE parent_id = :parentId")
    fun getChipPathsLive(parentId: Long): LiveData<List<ChipPath>>

    @Query("SELECT is_topic FROM chips WHERE id = :id")
    fun isChipTopic(id: Long): Boolean
}