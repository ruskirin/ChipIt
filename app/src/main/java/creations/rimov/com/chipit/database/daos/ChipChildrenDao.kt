package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.objects.ChipPath
import creations.rimov.com.chipit.objects.CoordPoint

@Dao
interface ChipChildrenDao : BaseChipDao {

    @Query("SELECT id, parent_id, is_topic, image_location FROM chips WHERE id = :id")
    fun getChipIdentity(id: Long): ChipIdentity

    @Query("SELECT id, parent_id, is_topic, image_location FROM chips WHERE id = :id")
    fun getChipIdentityLive(id: Long): LiveData<ChipIdentity>

    @Transaction
    @Query("SELECT * FROM chips WHERE parent_id = :parentId")
    fun getChipChildrenLive(parentId: Long): LiveData<List<Chip>>

    @Transaction
    @Query("SELECT id, parent_id, name, image_location FROM chips WHERE parent_id = :parentId")
    fun getChipChildrenCards(parentId: Long): List<ChipCard>

    @Transaction
    @Query("SELECT id, parent_id, name, image_location FROM chips WHERE parent_id = :parentId")
    fun getChipChildrenCardsLive(parentId: Long): LiveData<List<ChipCard>>

    @Transaction
    @Query("SELECT id, image_location, vertices FROM chips WHERE parent_id = :parentId")
    fun getChipChildrenPaths(parentId: Long): LiveData<List<ChipPath>>

    @Query("SELECT is_topic FROM chips WHERE id = :id")
    fun isChipTopic(id: Long): Boolean
}