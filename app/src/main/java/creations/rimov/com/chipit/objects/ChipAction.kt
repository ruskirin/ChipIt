package creations.rimov.com.chipit.objects

import creations.rimov.com.chipit.database.objects.Chip

object ChipAction {
    private var chip: Chip? = null
    private var action: Int = 0

    fun instance(chip: Chip, action: Int): ChipAction {
        this.chip = chip
        this.action = action

        return this
    }

    fun getChip() = chip

    fun getAction() = action
}