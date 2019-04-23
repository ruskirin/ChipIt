package creations.rimov.com.chipit.database.objects

import androidx.lifecycle.LiveData
import androidx.room.Embedded
import androidx.room.Relation
import creations.rimov.com.chipit.database.objects.Chip

data class ChipAndChildren(

    /** For reference:  https://android.jlelse.eu/exploring-room-architecture-component-6db807094241 **/
    @Embedded
    var parent: Chip,

    @Relation(parentColumn = "id", entityColumn = "parent_id", entity = Chip::class)
    val children: LiveData<List<Chip>>)