package creations.rimov.com.chipit.database.repos

import creations.rimov.com.chipit.database.objects.ChipIdentity

interface RepoHandler {

    fun getParent(chip: ChipIdentity)
}