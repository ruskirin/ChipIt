package creations.rimov.com.chipit.database.objects

import androidx.room.ColumnInfo

data class ChipCard(val id: Long,
                    @ColumnInfo(name = "description") val description: String,
                    @ColumnInfo(name = "image_location") val imgLocation: String)