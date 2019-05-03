package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import creations.rimov.com.chipit.database.objects.Chip

@Dao
interface ChipChildrenDao {

    @Transaction
    @Query("SELECT * FROM chips WHERE parent_id = :parentId")
    fun getAllChildren(parentId: Long): LiveData<List<Chip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChip(chip: Chip): Long

    @Delete
    fun deleteChip(vararg chip: Chip): Int
}