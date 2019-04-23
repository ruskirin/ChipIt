package creations.rimov.com.chipit.database.daos

import androidx.room.Dao
import androidx.room.Query
import creations.rimov.com.chipit.objects.Point

@Dao
interface ChipDao {

    @Query("UPDATE chips SET name = :name WHERE id = :id")
    fun updateName(id: Long, name: String): Int

    @Query("UPDATE chips SET image_path = :imagePath WHERE id = :id")
    fun updateImage(id: Long, imagePath: String): Int

    @Query("UPDATE chips SET vertices = :vertices WHERE id = :id")
    fun updateVertices(id: Long, vertices: List<Point>)
}