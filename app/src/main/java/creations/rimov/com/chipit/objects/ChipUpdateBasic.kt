package creations.rimov.com.chipit.objects

import creations.rimov.com.chipit.database.objects.Chip

object ChipUpdateBasic {

    var id: Long = 0L
    var parentId: Long? = null
    var isTopic: Boolean = false
    var name: String = ""
    var desc: String = ""
    var imgLocation: String = ""
    var vertices: MutableList<CoordPoint>? = null

    fun instance(id: Long, parentId: Long?, isTopic: Boolean = false,
                 name: String = "", desc: String = "", imgLocation: String = "", vertices: MutableList<CoordPoint>? = null): ChipUpdateBasic {

        ChipUpdateBasic.id = id
        ChipUpdateBasic.parentId = parentId
        ChipUpdateBasic.isTopic = isTopic
        ChipUpdateBasic.name = name
        ChipUpdateBasic.desc = desc
        ChipUpdateBasic.imgLocation = imgLocation
        ChipUpdateBasic.vertices = vertices

        return this
    }

    fun toChip() =
        Chip(
            id,
            parentId,
            isTopic = isTopic,
            name = name,
            desc = desc,
            imgLocation = imgLocation,
            vertices = vertices
        )
}