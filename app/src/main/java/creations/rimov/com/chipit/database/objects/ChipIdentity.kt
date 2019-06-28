package creations.rimov.com.chipit.database.objects

import androidx.room.ColumnInfo

data class ChipIdentity(
    val id: Long,
    @ColumnInfo(name = "parent_id") val parentId: Long?,
    @ColumnInfo(name = "is_topic") val isTopic: Boolean,
    val name: String,
    @ColumnInfo(name = "description") var desc: String,
    @ColumnInfo(name = "date_create") val dateCreate: String,
    @ColumnInfo(name = "num_children") var counter: Int,
    @ColumnInfo(name = "image_location") var imgLocation: String) {

    fun getChip() = Chip(id, parentId, isTopic,
            name, desc, dateCreate, counter = counter, imgLocation = imgLocation)
}