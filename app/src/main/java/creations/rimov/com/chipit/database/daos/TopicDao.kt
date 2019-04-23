package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import creations.rimov.com.chipit.database.objects.Topic

@Dao
interface TopicDao {

    @Query("SELECT * FROM topics")
    fun getAll(): LiveData<List<Topic>>

    @Query("SELECT * FROM topics WHERE id = :id")
    fun getOne(id: Long): LiveData<Topic>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTopic(topic: Topic)

    @Query("UPDATE topics SET image_path = :imagePath WHERE id = :id")
    fun updateImage(id: Long, imagePath: String): Int

    @Query("UPDATE topics SET name = :name WHERE id = :id")
    fun updateName(id: Long, name: String): Int

    @Delete
    fun deleteTopic(vararg topics: Topic): Int
}