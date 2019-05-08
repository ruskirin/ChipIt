package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.objects.Point

@Dao
interface ChipDao {

    @Query("SELECT * FROM chips WHERE id = :id")
    fun getChip(id: Long): LiveData<Chip>

    @Query("UPDATE chips SET name = :name WHERE id = :id")
    fun updateName(id: Long, name: String): Int

    @Query("UPDATE chips SET image_location = :imgLocation WHERE id = :id")
    fun updateImage(id: Long, imgLocation: String): Int

    @Query("UPDATE chips SET vertices = :vertices WHERE id = :id")
    fun updateVertices(id: Long, vertices: List<Point>)
}