package creations.rimov.com.chipit.database.daos

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import creations.rimov.com.chipit.database.objects.Chip

interface BaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChip(chip: Chip): Long

    @Delete
    fun deleteChip(vararg chip: Chip): Int
}