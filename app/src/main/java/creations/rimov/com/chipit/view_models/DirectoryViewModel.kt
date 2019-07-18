package creations.rimov.com.chipit.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipTopic
import creations.rimov.com.chipit.database.repos.AccessRepo

class DirectoryViewModel : ViewModel(), AccessRepo.RepoHandler {

    private val repository = AccessRepo(DatabaseApplication.database!!, this)

    private val topics: LiveData<List<ChipTopic>> = repository.getChipTopics()

    private val children: MutableLiveData<List<ChipCard>> = MutableLiveData()


    fun getTopics() = topics

    fun getChildren() = children

    fun setTopicChildren(id: Long) {
        repository.getChipCards(id)
    }

    override fun <T> setData(data: T) {

    }

    override fun <T> setDataList(data: List<T>) {

        if(data.isEmpty()) return

        if(data[0] is ChipCard) children.postValue(data as List<ChipCard>)
    }
}