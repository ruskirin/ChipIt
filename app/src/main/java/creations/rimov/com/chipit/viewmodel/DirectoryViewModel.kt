package creations.rimov.com.chipit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.data.DatabaseApplication
import creations.rimov.com.chipit.data.objects.ChipCard
import creations.rimov.com.chipit.data.objects.ChipIdentity
import creations.rimov.com.chipit.data.repos.AccessRepo
import creations.rimov.com.chipit.data.repos.RepoHandler

class DirectoryViewModel : ViewModel(), RepoHandler {

    private val repository = AccessRepo(DatabaseApplication.database!!, this)

    private val topics: LiveData<List<ChipIdentity>> = repository.getChipTopics()

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