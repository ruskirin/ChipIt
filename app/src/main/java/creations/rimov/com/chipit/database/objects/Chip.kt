package creations.rimov.com.chipit.database.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import creations.rimov.com.chipit.extensions.getChipCreateDate
import creations.rimov.com.chipit.extensions.getChipUpdateDate
import creations.rimov.com.chipit.objects.CoordPoint
import java.util.*

@Entity(
    tableName = "chips",
    indices = [Index(value = ["id"], unique = true)]
)

//TODO IMPORTANT: SimpleDateFormat is supposedly expensive, move it out of the fields and have it called on background thread
data class Chip(@PrimaryKey(autoGenerate = true) val id: Long,

                @ColumnInfo(name = "parent_id") var parentId: Long? = null,

                @ColumnInfo(name = "is_topic") var isTopic: Boolean = false,

                var name: String = "",

                @ColumnInfo(name = "description") var desc: String = "",

                @ColumnInfo(name = "date_create") val created: String =
                    Date().getChipCreateDate(),

                @ColumnInfo(name = "date_update") var updated: String = "",

                @ColumnInfo(name = "num_children") var counter: Int = 0,

                @ColumnInfo(name = "image_location") var imgLocation: String = "",

                var vertices: MutableList<CoordPoint>? = null)