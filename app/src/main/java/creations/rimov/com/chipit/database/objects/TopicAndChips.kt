package creations.rimov.com.chipit.database.objects

import androidx.room.Embedded
import androidx.room.Relation

data class TopicAndChips(
    @Embedded
    var parent: Topic,

    @Relation(parentColumn = "id", entityColumn = "parent_id", entity = Chip::class)
    val children: MutableList<Chip>)