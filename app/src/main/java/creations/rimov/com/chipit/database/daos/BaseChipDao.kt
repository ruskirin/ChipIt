package creations.rimov.com.chipit.database.daos

import androidx.room.*
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.objects.CoordPoint

interface BaseChipDao {

    @Query("SELECT * FROM chips WHERE id = :id")
    fun getChip(id: Long): Chip

    @Query("SELECT image_location FROM chips WHERE id = :id")
    fun getChipImage(id: Long): String

    @Transaction
    @Query("SELECT id FROM chips WHERE parent_id = :id")
    fun getChildrenIds(id: Long): List<Long>

    @Query("UPDATE chips SET description = :desc WHERE id = :id")
    fun updateDescription(id: Long, desc: String): Int

    @Query("UPDATE chips SET image_location = :imgLocation WHERE id = :id")
    fun updateImage(id: Long, imgLocation: String): Int

    @Query("UPDATE chips SET vertices = :vertices WHERE id = :id")
    fun updateVertices(id: Long, vertices: List<CoordPoint>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChip(chip: Chip): Long

    @Query("DELETE FROM chips WHERE id = :chipId")
    fun deleteChip(chipId: Long): Int

    @Delete
    fun deleteChip(vararg chips: Chip): Int
}