package creations.rimov.com.chipit.database.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import creations.rimov.com.chipit.objects.CoordPoint

@Entity(
    tableName = "chips",
    indices = [Index(value = ["id"], unique = true)]
)

data class Chip(

    @PrimaryKey(autoGenerate = true) val id: Long,

    @ColumnInfo(name = "parent_id") val parentId: Long,

    @ColumnInfo(name = "is_topic") val isTopic: Boolean = false,

    var name: String = "NAME",

    @ColumnInfo(name = "description") var desc: String,

    @ColumnInfo(name = "date_create") val created: String,

    @ColumnInfo(name = "date_update") var updated: String = "",

    @ColumnInfo(name = "num_children") var counter: Int = 0,

    @ColumnInfo(name = "image_location") var imgLocation: String = "",

    val vertices: List<CoordPoint>? = mutableListOf())