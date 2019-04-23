package creations.rimov.com.chipit.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Topic
import creations.rimov.com.chipit.database.repos.TopicRepository

class DirectoryViewModel : ViewModel() {

    private val topicRepo: TopicRepository = TopicRepository(DatabaseApplication.database!!)

    private val topics: LiveData<List<Topic>> = topicRepo.getAll()

    //TODO: if no LiveData-esque use is found, change to regular bool variables
    val topicPressed = MutableLiveData<Boolean>(false)
    val topicLongPressed = MutableLiveData<Boolean>(false)


    fun handleTopicTouch(type: Int) {

        when(type) {
            1 -> {
                topicPressed.value = true
            }
            2 -> {
                topicLongPressed.value = true
                topicPressed.value = false
            }
        }
    }

    /** Repository functions **/
    fun getTopics() = topics

    fun getTopic(position: Int) = topics.value?.get(position)

    fun insertTopic(topic: Topic) {
        topicRepo.insert(topic)
    }

    fun updateTopicName(id: Long, name: String) {
        topicRepo.update(id, name)
    }

    fun updateTopicImage(id: Long, imagePath: String) {
        topicRepo.update(id, "", imagePath)
    }

    fun deleteTopic(topic: Topic) {
        topicRepo.delete(topic)
    }
}