package creations.rimov.com.chipit.database.objects

import androidx.room.ColumnInfo

data class ChipTopic(val id: Long,
                     val name: String = "NAME",
                     @ColumnInfo(name = "num_children") val counter: Int = 0,
                     @ColumnInfo(name = "description") val desc: String = "",
                     @ColumnInfo(name = "date") val date: String,
                     @ColumnInfo(name = "rep_path") val repPath: String = "") {

    fun asChip() = Chip(id, name = name, desc = desc,
                        date = date, counter = counter, repPath = repPath)
}