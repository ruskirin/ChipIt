package creations.rimov.com.chipit.database.objects

import androidx.room.ColumnInfo

data class ChipReference(val id: Long,
                         @ColumnInfo(name = "parent_id") val parentId: Long?,
                         val name: String,
                         @ColumnInfo(name = "rep_path") val repPath: String) {

    fun asChip() = Chip(id, parentId, name = name, repPath = repPath)
}