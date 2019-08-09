package creations.rimov.com.chipit.database.objects

import androidx.room.ColumnInfo

data class ChipTopic(val id: Long,
                     val name: String = "NAME",
                     @ColumnInfo(name = "num_children") val counter: Int = 0,
                     @ColumnInfo(name = "description") val desc: String = "",
                     @ColumnInfo(name = "date_create") val dateCreate: String,
                     @ColumnInfo(name = "date_update") val dateUpdate: String = "") {

    fun asChip() = Chip(id, isTopic = true,
                        name = name, desc = desc, created =  dateCreate, counter = counter)
}