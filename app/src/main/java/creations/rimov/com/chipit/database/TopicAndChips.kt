package creations.rimov.com.chipit.database

import androidx.room.Embedded
import androidx.room.Relation

class TopicAndChips {

    @Embedded
    var parent: Topic? = null

    @Relation(parentColumn = "id", entityColumn = "parent_id", entity = Chip::class)
    val children = mutableListOf<Chip>()
}