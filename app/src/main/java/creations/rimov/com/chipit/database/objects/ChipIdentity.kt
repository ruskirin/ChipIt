package creations.rimov.com.chipit.database.objects

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChipIdentity(
  val id: Long,
  @ColumnInfo(name = "parent_id") val parentId: Long?,
  val name: String,
  @ColumnInfo(name = "description") val desc: String,
  @ColumnInfo(name = "date") val date: String,
  @ColumnInfo(name = "num_children") val numChildren: Int,
  @ColumnInfo(name = "material_type") val matType: Int,
  @ColumnInfo(name = "material_path") val matPath: String) : Parcelable, ChipConvert {

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