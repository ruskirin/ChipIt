package creations.rimov.com.chipit.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chips",
    indices = [Index(value = ["id"], unique = true)]
)

data class Chip(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "parent_chip_id") val parentId: Long,
    var name: String?,
    @ColumnInfo(name = "image_path") var imagePath: String)
