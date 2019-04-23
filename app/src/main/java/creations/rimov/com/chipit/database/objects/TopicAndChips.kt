package creations.rimov.com.chipit.database.objects

import androidx.lifecycle.LiveData
import androidx.room.Embedded
import androidx.room.Relation

data class TopicAndChips(
    @Embedded
    val parent: Topic,

    @Relation(parentColumn = "id", entityColumn = "parent_id", entity = Chip::class)
    val children: LiveData<List<Chip>>)