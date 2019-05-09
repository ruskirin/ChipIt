package creations.rimov.com.chipit.database.objects

import androidx.room.ColumnInfo

data class ChipCard(val id: Long,
                    val name: String?,
                    @ColumnInfo(name = "image_location") val imgLocation: String)