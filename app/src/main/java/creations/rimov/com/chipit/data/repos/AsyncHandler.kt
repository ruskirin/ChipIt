package creations.rimov.com.chipit.data.repos

interface AsyncHandler {

    fun <T> setData(data: T)
}