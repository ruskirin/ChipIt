package creations.rimov.com.chipit.database.objects

import androidx.room.ColumnInfo

data class ChipIdentity(
      val id: Long,
      @ColumnInfo(name = "parent_id") val parentId: Long?,
      val name: String,
      @ColumnInfo(name = "description") var desc: String,
      @ColumnInfo(name = "date") val date: String,
      @ColumnInfo(name = "num_children") var counter: Int,
      @ColumnInfo(name = "rep_path") var repPath: String) {

    fun asChip() = Chip(id, parentId, name, desc, date,
                        counter = counter, repPath = repPath)
}