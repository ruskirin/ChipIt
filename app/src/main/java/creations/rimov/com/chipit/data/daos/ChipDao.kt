package creations.rimov.com.chipit.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import creations.rimov.com.chipit.data.objects.*
import creations.rimov.com.chipit.util.objects.CoordPoint

@Dao
interface ChipDao {

    @Query("SELECT * FROM chips WHERE id = :id")
    fun getChip(id: Long): Chip

    @Transaction
    @Query("SELECT * FROM chips WHERE parent_id = :parentId")
    fun getChipChildrenLive(parentId: Long): LiveData<List<Chip>>

    @Query("WITH RECURSIVE get_parents(id, parent_id, name, material_type, material_path) " +
            "AS (SELECT id, parent_id, name, material_type, material_path FROM chips WHERE id = :id " +
            "UNION ALL " +
            "SELECT get_parents.parent_id, chips.parent_id, chips.name, chips.material_type, chips.material_path " +
            "FROM chips, get_parents " +
            "WHERE chips.id = get_parents.parent_id) " +
            "SELECT * FROM get_parents;")
    fun getChipReferenceParentTreeLive(id: Long?): LiveData<List<ChipReference>>

    @Transaction
    @Query("SELECT id, name, description, num_children, material_type, material_path FROM chips WHERE parent_id = :parentId")
    fun getChipCards(parentId: Long): List<ChipCard>

    @Transaction
    @Query("SELECT id, name, description, num_children, material_type, material_path FROM chips WHERE parent_id = :parentId")
    fun getChipCardsLive(parentId: Long): LiveData<List<ChipCard>>

    @Transaction
    @Query("SELECT id, name, description, num_children, material_type, material_path FROM chips WHERE parent_id IS NULL")
    fun getChipCardsOfNullLive(): LiveData<List<ChipCard>>

    @Query("SELECT id, parent_id, name, description, date, num_children, material_type, material_path FROM chips WHERE id = :id")
    fun getChipIdentity(id: Long): ChipIdentity

    @Query("SELECT id, parent_id, name, description, date, num_children, material_type, material_path FROM chips WHERE id = :id")
    fun getChipIdentityLive(id: Long): LiveData<ChipIdentity>

    @Transaction
    @Query("SELECT id, parent_id, name, description, date, num_children, material_type, material_path FROM chips WHERE parent_id IS NULL")
    fun getChipTopicsLive(): LiveData<List<ChipIdentity>>

    @Transaction
    @Query("SELECT id, material_type, material_path, vertices FROM chips WHERE parent_id = :parentId")
    fun getChipPathsLive(parentId: Long?): LiveData<List<ChipPath>>

    //@Query("SELECT is_topic FROM chips WHERE id = :id")
    //fun isChipTopic(id: Long): Boolean

    @Query("SELECT material_path FROM chips WHERE id = :id")
    fun getChipImage(id: Long): String

    @Query("WITH RECURSIVE " +
            "get_children(x) AS (" +
            "SELECT :parentId UNION ALL " +
            "SELECT id FROM chips, get_children WHERE parent_id = get_children.x) " +
            "SELECT * FROM get_children;")
    fun getBranchChildrenIds(parentId: Long): List<Long>

    @Query("WITH RECURSIVE " +
            "get_parents(x) AS (" +
            "SELECT :childId UNION ALL " +
            "SELECT parent_id FROM chips, get_parents WHERE id = get_parents.x AND parent_id IS NOT NULL) " +
            "SELECT * FROM get_parents;")
    fun getBranchParentIds(childId: Long): List<Long>

    @Query("WITH RECURSIVE " +
            "get_parents(x) AS (" +
            "SELECT :id UNION ALL SELECT parent_id FROM chips, get_parents " +
            "WHERE id = get_parents.x AND parent_id IS NOT NULL) " +
            "UPDATE chips SET date = :date WHERE id IN get_parents;")
    fun setUpdateDate(id: Long, date: String)

    /**Starting from id, increase the counter up the parent tree by amt**/
    @Query("WITH RECURSIVE " +
            "get_parents(x) AS (" +
            "SELECT :id UNION ALL SELECT parent_id FROM chips, get_parents " +
            "WHERE id = get_parents.x AND parent_id IS NOT NULL) " +
            "UPDATE chips SET num_children = num_children + :amt, date = :date " +
            "WHERE id IN get_parents;")
    fun increaseCounter(id: Long, amt: Int, date: String)

    /**Starting from id, decrease the counter up the parent tree by amt**/
    @Query("WITH RECURSIVE " +
            "get_parents(x) AS (" +
            "SELECT :id UNION ALL SELECT parent_id FROM chips, get_parents " +
            "WHERE id = get_parents.x AND parent_id IS NOT NULL) " +
            "UPDATE chips SET num_children = num_children - :amt, date = :date " +
            "WHERE id IN get_parents;")
    fun decreaseCounter(id: Long, amt: Int, date: String)

    @Query("UPDATE chips SET vertices = :vertices WHERE id = :id")
    fun updateVertices(id: Long, vertices: List<CoordPoint>)

    /**Update the standard information**/
    @Query("UPDATE chips SET name = :name, description = :desc, material_path = :matPath WHERE id = :id")
    fun updateChipBasic(id: Long, name: String, desc: String, matPath: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChip(chip: Chip): Long

    @Query("WITH RECURSIVE " +
            "get_children(x) AS (" +
            "SELECT :id UNION ALL SELECT id FROM chips, get_children " +
            "WHERE parent_id = get_children.x) " +
            "DELETE FROM chips WHERE id IN get_children;")
    fun deleteChipTree(id: Long)

    @Query("DELETE FROM chips WHERE id = :id")
    fun deleteChip(id: Long): Int
}