package creations.rimov.com.chipit.database.repos

interface RepoHandler : AsyncHandler {

    fun <T> setDataList(data: List<T>)
}