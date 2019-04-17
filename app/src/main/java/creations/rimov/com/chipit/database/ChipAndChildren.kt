package creations.rimov.com.chipit.database

import androidx.room.Embedded
import androidx.room.Relation

class ChipAndChildren {

    /** For reference:  https://stackoverflow.com/a/44424148/8916812 **/

    @Embedded
    var parent: Chip? = null

    @Relation(parentColumn = "id", entityColumn = "parent_chip_id", entity = Chip::class)
    val children = mutableListOf<Chip>()
}