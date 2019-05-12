package creations.rimov.com.chipit.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Topic
import creations.rimov.com.chipit.database.repos.TopicRepository
import creations.rimov.com.chipit.objects.DirectoryTouchFlag

class DirectoryViewModel : ViewModel() {

    private val topicRepo: TopicRepository = TopicRepository(DatabaseApplication.database!!)

    private val topics: LiveData<List<Topic>> = topicRepo.getAll()

    val touchFlag = MutableLiveData<DirectoryTouchFlag>()


    /** Repository functions **/
    fun getTopics() = topics

    fun getTopic(position: Int) = topics.value?.get(position)

    fun insertTopic(topic: Topic) {
        topicRepo.insert(topic)
    }

    fun updateTopicName(id: Long, name: String) {
        topicRepo.update(id, name)
    }

    fun updateTopicImage(id: Long, imgLocation: String) {
        topicRepo.update(id, "", imgLocation)
    }

    fun deleteTopic(topic: Topic) {
        topicRepo.delete(topic)
    }

    fun setTopicTouch(touched: Boolean) {
        touchFlag.postValue(DirectoryTouchFlag(true))
    }

    fun setTopicLongTouch(touched: Boolean) {
        touchFlag.postValue(DirectoryTouchFlag(false, true, false))
    }

    fun setFabTouch(touched: Boolean) {
        touchFlag.postValue(DirectoryTouchFlag(false, false, true))
    }
}