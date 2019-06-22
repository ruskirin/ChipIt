package creations.rimov.com.chipit.database.objects

import androidx.room.ColumnInfo

data class ChipDelete(val id: Long,
                      @ColumnInfo(name = "image_location") val imgLocation: String)