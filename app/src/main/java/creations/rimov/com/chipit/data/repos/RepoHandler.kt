package creations.rimov.com.chipit.data.repos

interface RepoHandler : AsyncHandler {

    fun <T> setDataList(data: List<T>)
}