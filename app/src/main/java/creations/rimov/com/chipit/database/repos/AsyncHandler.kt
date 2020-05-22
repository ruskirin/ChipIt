package creations.rimov.com.chipit.database.repos

interface AsyncHandler {

    fun <T> setData(data: T)
}