package creations.rimov.com.chipit.database.objects

import androidx.room.Embedded
import androidx.room.Relation

data class TopicAndChips(

    @Embedded
    val topic: Topic,

    @Relation(parentColumn = "id", entityColumn = "parent_id", entity = Chip::class)
    val chips: List<Chip>
)
