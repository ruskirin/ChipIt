package creations.rimov.com.chipit.database.objects

import androidx.room.ColumnInfo

data class ChipReference(val id: Long,
                         @ColumnInfo(name = "parent_id") val parentId: Long?,
                         val name: String,
                         @ColumnInfo(name = "description") val desc: String,
                         @ColumnInfo(name = "image_location") val imgLocation: String)