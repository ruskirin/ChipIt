package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.objects.ChipPath

@Dao
interface ChipChildrenDao {

    @Transaction
    @Query("SELECT * FROM chips WHERE parent_id = :parentId")
    fun getChipChildren(parentId: Long): LiveData<List<Chip>>

    @Transaction
    @Query("SELECT id, parent_id, name, image_location FROM chips WHERE parent_id = :parentId")
    fun getChipChildrenCards(parentId: Long): List<ChipCard>

    @Transaction
    @Query("SELECT id, parent_id, name, image_location FROM chips WHERE parent_id = :parentId")
    fun getChipChildrenCardsLive(parentId: Long): LiveData<List<ChipCard>>

    @Transaction
    @Query("SELECT id, image_location, vertices FROM chips WHERE parent_id = :parentId")
    fun getChipChildrenPaths(parentId: Long): LiveData<List<ChipPath>>

    @Query("SELECT * FROM chips WHERE id = :id")
    fun getChip(id: Long): Chip

    @Query("SELECT id, parent_id, is_topic, image_location FROM chips WHERE id = :id")
    fun getChipIdentity(id: Long): ChipIdentity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChip(chip: Chip): Long

    @Delete
    fun deleteChip(vararg chip: Chip): Int
}