package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard

@Dao
interface ChipChildrenDao {

    @Transaction
    @Query("SELECT * FROM chips WHERE parent_id = :parentId")
    fun getChipChildren(parentId: Long): LiveData<List<Chip>>

    @Transaction
    @Query("SELECT id, name, image_location FROM chips WHERE parent_id = :parentId")
    fun getChipChildrenCards(parentId: Long): LiveData<List<ChipCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChip(chip: Chip): Long

    @Delete
    fun deleteChip(vararg chip: Chip): Int
}