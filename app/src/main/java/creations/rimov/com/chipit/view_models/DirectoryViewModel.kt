package creations.rimov.com.chipit.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.ChipTopic
import creations.rimov.com.chipit.database.repos.AccessRepo

class DirectoryViewModel : ViewModel(), AccessRepo.RepoHandler {

    private val repository = AccessRepo(DatabaseApplication.database!!, this)

    private val topics: LiveData<List<ChipTopic>> = repository.getChipTopics()


    fun getTopics() = topics

    override fun <T> setData(data: T) {
//        this.topics.postValue(data as List<TopicAndChildren>)
    }
}