package creations.rimov.com.chipit.database.objects

import androidx.room.ColumnInfo

data class ChipCard(val id: Long,
                    val name: String,
                    @ColumnInfo(name = "num_children") val counter: Int,
                    @ColumnInfo(name = "image_location") val imgLocation: String)