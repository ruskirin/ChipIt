package creations.rimov.com.chipit.database.objects

interface ChipConvert {

    fun asChip(parentId: Long? = null): Chip
}