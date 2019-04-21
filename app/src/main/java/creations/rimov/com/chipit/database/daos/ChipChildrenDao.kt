package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import creations.rimov.com.chipit.database.objects.ChipAndChildren

@Dao
interface ChipChildrenDao : BaseDao {

    @Transaction
    @Query("SELECT * FROM chips WHERE parent_chip_id = :parentId")
    fun getAllChildren(parentId: Long): LiveData<ChipAndChildren>
}