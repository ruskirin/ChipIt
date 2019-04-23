package creations.rimov.com.chipit.database.objects

import androidx.room.*
import creations.rimov.com.chipit.objects.Point

@Entity(
    tableName = "chips",
    indices = [Index(value = ["id"], unique = true)]
)

data class Chip(

    @PrimaryKey(autoGenerate = true) val id: Long,

    @ColumnInfo(name = "parent_id") val parentId: Long,

    var name: String?,

    @ColumnInfo(name = "image_path") var imagePath: String,

    var vertices: MutableList<Point>?)
