package creations.rimov.com.chipit.database.objects

import androidx.room.ColumnInfo

data class ChipIdentity(
    val id: Long,
    @ColumnInfo(name = "parent_id") val parentId: Long,
    @ColumnInfo(name = "is_topic") val isTopic: Boolean)