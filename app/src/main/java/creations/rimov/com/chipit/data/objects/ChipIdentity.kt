package creations.rimov.com.chipit.data.objects

import androidx.room.ColumnInfo

data class ChipIdentity(
  val id: Long,
  @ColumnInfo(name = "parent_id") val parentId: Long?,
  val name: String,
  @ColumnInfo(name = "description") val desc: String,
  @ColumnInfo(name = "date") val date: String,
  @ColumnInfo(name = "num_children") val numChildren: Int,
  @ColumnInfo(name = "material_type") val matType: Int,
  @ColumnInfo(name = "material_path") val matPath: String) : ChipConvert {

    override fun asChip(parentId: Long?): Chip = Chip(
      id,
      this.parentId,
      name,
      desc,
      date,
      numChildren = numChildren,
      matType = matType,
      matPath = matPath)
}