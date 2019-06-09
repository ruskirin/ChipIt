package creations.rimov.com.chipit.database.daos

import androidx.room.*
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.objects.CoordPoint

interface BaseChipDao {

    @Query("SELECT * FROM chips WHERE id = :id")
    fun getChip(id: Long): Chip

    @Transaction
    @Query("SELECT id FROM chips WHERE parent_id = :id")
    fun getChildrenIds(id: Long): List<Long>

    @Query("UPDATE chips SET name = :name WHERE id = :id")
    fun updateName(id: Long, name: String): Int

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