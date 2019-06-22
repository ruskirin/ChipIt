package creations.rimov.com.chipit.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import creations.rimov.com.chipit.database.objects.*
import creations.rimov.com.chipit.objects.CoordPoint

@Dao
interface ChipDao {

    @Transaction
    @Query("SELECT * FROM chips WHERE parent_id = :parentId")
    fun getChipsLive(parentId: Long): LiveData<List<Chip>>

    @Transaction
    @Query("SELECT id, name, num_children, description, date_create, date_update FROM chips WHERE is_topic")
    fun getChipTopics(): List<ChipTopic>

    @Transaction
    @Query("SELECT id, name, num_children, description, date_create, date_update FROM chips WHERE is_topic")
    fun getChipTopicsLive(): LiveData<List<ChipTopic>>

    @Transaction
    @Query("SELECT id, name, num_children, image_location FROM chips WHERE parent_id = :parentId")
    fun getChipCards(parentId: Long): List<ChipCard>

    @Transaction
    @Query("SELECT id, name, num_children, image_location FROM chips WHERE parent_id = :parentId")
    fun getChipCardsLive(parentId: Long): LiveData<List<ChipCard>>

    @Query("SELECT id, parent_id, is_topic, name, description, date_create, num_children, image_location FROM chips WHERE id = :id")
    fun getChipIdentity(id: Long): ChipIdentity

    @Query("SELECT id, parent_id, is_topic, name, description, date_create, num_children, image_location FROM chips WHERE id = :id")
    fun getChipIdentityLive(id: Long): LiveData<ChipIdentity>

    @Transaction
    @Query("SELECT id, image_location, vertices FROM chips WHERE parent_id = :parentId")
    fun getChipPathsLive(parentId: Long): LiveData<List<ChipPath>>

    @Query("SELECT is_topic FROM chips WHERE id = :id")
    fun isChipTopic(id: Long): Boolean

    @Query("SELECT image_location FROM chips WHERE id = :id")
    fun getChipImage(id: Long): String

    @Query("WITH RECURSIVE " +
            "get_children(x) AS (SELECT :parentId UNION ALL SELECT id FROM chips, get_children WHERE parent_id = get_children.x) " +
            "SELECT * FROM get_children;")
    fun getAllChildrenIds(parentId: Long): List<Long>

    @Query("WITH RECURSIVE " +
            "get_parents(x) AS (SELECT :childId UNION ALL SELECT parent_id FROM chips, get_parents WHERE id = get_parents.x AND parent_id IS NOT NULL) " +
            "SELECT * FROM get_parents;")
    fun getAllParentIds(childId: Long): List<Long>

    /**Starting from id, increase the counter up the parent tree by amt**/
    @Query("WITH RECURSIVE " +
            "get_parents(x) AS (SELECT :id UNION ALL SELECT parent_id FROM chips, get_parents WHERE id = get_parents.x AND parent_id IS NOT NULL) " +
            "UPDATE chips SET num_children = num_children + :amt WHERE id IN get_parents;")
    fun increaseCounter(id: Long, amt: Int)

    /**Starting from id, decrease the counter up the parent tree by amt**/
    @Query("WITH RECURSIVE " +
            "get_parents(x) AS (SELECT :id UNION ALL SELECT parent_id FROM chips, get_parents WHERE id = get_parents.x AND parent_id IS NOT NULL) " +
            "UPDATE chips SET num_children = num_children - :amt WHERE id IN get_parents;")
    fun decreaseCounter(id: Long, amt: Int)

    @Query("UPDATE chips SET vertices = :vertices WHERE id = :id")
    fun updateVertices(id: Long, vertices: List<CoordPoint>)

    /**Update the standard information**/
    @Query("UPDATE chips SET name = :name, description = :desc, image_location = :imgLocation WHERE id = :id")
    fun updateChipBasic(id: Long, name: String, desc: String, imgLocation: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChip(chip: Chip): Long

    @Query("WITH RECURSIVE " +
            "get_children(x) AS (SELECT :id UNION ALL SELECT id FROM chips, get_children WHERE parent_id = get_children.x) " +
            "DELETE FROM chips WHERE id IN get_children;")
    fun deleteChipTree(id: Long)

    @Query("DELETE FROM chips WHERE id = :id")
    fun deleteChip(id: Long): Int
}