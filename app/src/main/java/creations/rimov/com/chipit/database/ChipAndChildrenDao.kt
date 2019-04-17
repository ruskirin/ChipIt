package creations.rimov.com.chipit.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ChipAndChildrenDao {

    @Transaction @Query("SELECT * FROM chips")
    fun getAll(): LiveData<List<ChipAndChildren>>

    //TODO: might have to label as @Transaction if returned data is bad
    @Query("SELECT :id FROM chips")
    fun getOne(id: Long): LiveData<ChipAndChildren>

    @Insert
    fun insert(chip: Chip): Long
}