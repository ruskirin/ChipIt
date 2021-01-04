package creations.rimov.com.chipit.data.objects

import androidx.room.*
import creations.rimov.com.chipit.extension.getChipCreateDate
import creations.rimov.com.chipit.util.objects.CoordPoint
import java.util.*

@Entity(
    tableName = "chips",
    indices = [Index(value = ["id"], unique = true)]
)

//TODO IMPORTANT: SimpleDateFormat is supposedly expensive, move it out of
// the fields and have it called on background thread
data class Chip(
  @PrimaryKey(autoGenerate = true) val id: Long,

  @ColumnInfo(name = "parent_id") val parentId: Long? = null,

  val name: String = "",

  @ColumnInfo(name = "description") val desc: String = "",

  @ColumnInfo(name = "date") val date: String = Date().getChipCreateDate(),

  @ColumnInfo(name = "num_children") val numChildren: Int = 0,

  @ColumnInfo(name = "material_type") val matType: Int = 0,

  @ColumnInfo(name = "material_path") val matPath: String = "",

  val vertices: MutableList<CoordPoint>? = null)
