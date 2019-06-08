package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.objects.CoordPoint

@Dao
interface ChipDao {

    @Query("SELECT * FROM chips WHERE id = :id")
    fun getChip(id: Long): Chip

    @Query("SELECT is_topic FROM chips WHERE id = :parentId")
    fun isParentTopic(parentId: Long): Boolean

    @Query("UPDATE chips SET name = :name WHERE id = :id")
    fun updateName(id: Long, name: String): Int

    @Query("UPDATE chips SET image_location = :imgLocation WHERE id = :id")
    fun updateImage(id: Long, imgLocation: String): Int

    @Query("UPDATE chips SET vertices = :vertices WHERE id = :id")
    fun updateVertices(id: Long, vertices: List<CoordPoint>)
}