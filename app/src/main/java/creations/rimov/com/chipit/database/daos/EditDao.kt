package creations.rimov.com.chipit.database.daos

import androidx.room.*
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.objects.CoordPoint

@Dao
interface EditDao {

    @Query("SELECT image_location FROM chips WHERE id = :id")
    fun getChipImage(id: Long): String

//    @Query("SELECT id FROM chips WHERE parent_id = :parentId")
//    fun getChildrenIds(parentId: Long): List<Long>

    @Query("WITH RECURSIVE get_children(x) AS (SELECT :parentId UNION ALL SELECT id FROM chips, get_children WHERE parent_id = get_children.x) SELECT * FROM get_children;")
    fun getAllChildrenIds(parentId: Long): List<Long>

    @Query("WITH RECURSIVE get_parents(x) AS (SELECT :childId UNION ALL SELECT parent_id FROM chips, get_parents WHERE id = get_parents.x AND parent_id IS NOT NULL) SELECT * FROM get_parents;")
    fun getAllParentIds(childId: Long): List<Long>

    @Query("UPDATE chips SET name = :name WHERE id = :id")
    fun updateName(id: Long, name: String)

    @Query("UPDATE chips SET description = :desc WHERE id = :id")
    fun updateDescription(id: Long, desc: String)

    @Query("UPDATE chips SET date_update = :date WHERE id = :id")
    fun updateDate(id: Long, date: String)

    @Query("UPDATE chips SET image_location = :imgLocation WHERE id = :id")
    fun updateImage(id: Long, imgLocation: String): Int

    @Query("UPDATE chips SET vertices = :vertices WHERE id = :id")
    fun updateVertices(id: Long, vertices: List<CoordPoint>)

    @Update
    fun updateChip(chip: Chip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChip(chip: Chip): Long

    @Query("DELETE FROM chips WHERE id = :chipId")
    fun deleteChip(chipId: Long): Int

    @Delete
    fun deleteChip(vararg chips: Chip): Int
}