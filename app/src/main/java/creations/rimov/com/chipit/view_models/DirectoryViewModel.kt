package creations.rimov.com.chipit.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.repos.TopicChipRepository
import creations.rimov.com.chipit.objects.RecyclerTouchFlag

class DirectoryViewModel : ViewModel() {

    private val topicRepo: TopicChipRepository = TopicChipRepository(DatabaseApplication.database!!)

    private val topics: LiveData<List<Chip>> = topicRepo.getTopics()

    val topicTouch = MutableLiveData<RecyclerTouchFlag>()


    /** Repository functions **/
    fun getTopics() = topics

    fun getTopic(position: Int) = topics.value?.get(position)

    fun insertTopic(topic: Chip) {
        topicRepo.insert(topic)
    }

    fun updateTopicName(id: Long, name: String) {
        topicRepo.update(id, name, null)
    }

    fun updateTopicImage(id: Long, imgLocation: String) {
        topicRepo.update(id, "", imgLocation)
    }

    fun setTopicTouch() {
        topicTouch.postValue(RecyclerTouchFlag(true))
    }

    fun setTopicLongTouch() {
        topicTouch.postValue(RecyclerTouchFlag(false, true))
    }

    fun resetFlags() {
        topicTouch.postValue(RecyclerTouchFlag())
    }
}