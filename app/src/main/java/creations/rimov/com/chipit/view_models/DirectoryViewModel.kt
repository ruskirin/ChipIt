package creations.rimov.com.chipit.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Topic
import creations.rimov.com.chipit.database.repos.TopicRepository

class DirectoryViewModel : ViewModel() {

    private val topicRepo = TopicRepository(DatabaseApplication.database!!)

    private val topics: LiveData<List<Topic>> = topicRepo.getAll()


    fun getTopics() = topics

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