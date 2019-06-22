package creations.rimov.com.chipit.database.objects

object ChipUpdateBasic {

    var id: Long = -1L
    var name: String = ""
    var desc: String = ""
    var imgLocation: String = ""

    fun set(id: Long, name: String, desc: String, imgLocation: String): ChipUpdateBasic {

        this.id = id
        this.name = name
        this.desc = desc
        this.imgLocation = imgLocation

        return this
    }
}