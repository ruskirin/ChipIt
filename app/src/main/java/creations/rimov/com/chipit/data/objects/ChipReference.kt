package creations.rimov.com.chipit.data.objects

import androidx.room.ColumnInfo

data class ChipReference(
  val id: Long,
  @ColumnInfo(name = "parent_id") val parentId: Long?,
  val name: String,
  @ColumnInfo(name = "material_type") val matType: Int,
  @ColumnInfo(name = "material_path") val matPath: String) : ChipConvert {

    override fun asChip(parentId: Long?): Chip = Chip(
      id,
      parentId,
      name = name,
      matType = matType,
      matPath = matPath)
}