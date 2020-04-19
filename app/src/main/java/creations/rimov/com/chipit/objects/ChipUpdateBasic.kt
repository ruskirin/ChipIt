package creations.rimov.com.chipit.objects

import creations.rimov.com.chipit.database.objects.Chip

object ChipUpdateBasic {

    var id: Long = 0L
    var parentId: Long? = null
    var name: String = ""
    var desc: String = ""
    var repPath: String = ""
    var vertices: MutableList<CoordPoint>? = null

    fun instance(id: Long, parentId: Long?,
                 name: String = "",
                 desc: String = "",
                 repPath: String = "",
                 vertices: MutableList<CoordPoint>? = null): ChipUpdateBasic {

        ChipUpdateBasic.id = id
        ChipUpdateBasic.parentId = parentId
        ChipUpdateBasic.name = name
        ChipUpdateBasic.desc = desc
        ChipUpdateBasic.repPath = repPath
        ChipUpdateBasic.vertices = vertices

        return this
    }

    fun toChip() =
        Chip(
          id,
          parentId,
          name = name,
          desc = desc,
          repPath = repPath,
          vertices = vertices
        )
}