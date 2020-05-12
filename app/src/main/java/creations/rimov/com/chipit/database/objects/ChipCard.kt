package creations.rimov.com.chipit.database.objects

import androidx.room.ColumnInfo

data class ChipCard(
  val id: Long,
  val name: String,
  @ColumnInfo(name = "num_children") val numChildren: Int,
  @ColumnInfo(name = "material_type") val matType: Int,
  @ColumnInfo(name = "material_path") val matPath: String) : ChipConvert {


    override fun asChip(parentId: Long?) = Chip(
      id,
      parentId,
      name = name,
      numChildren = numChildren,
      matType = matType,
      matPath = matPath)
}