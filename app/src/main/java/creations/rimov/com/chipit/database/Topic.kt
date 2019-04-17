package creations.rimov.com.chipit.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "topics",
    indices = [Index(value = [], unique = true)]
)

data class Topic(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var name: String,
    @ColumnInfo(name = "image_path") var imagePath: String)