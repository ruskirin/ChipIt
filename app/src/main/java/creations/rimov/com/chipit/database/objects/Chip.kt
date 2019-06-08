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

    @ColumnInfo(name = "is_topic") val isTopic: Boolean,

    var name: String?,

    @ColumnInfo(name = "image_location") var imgLocation: String,

    var vertices: List<CoordPoint>? = mutableListOf())