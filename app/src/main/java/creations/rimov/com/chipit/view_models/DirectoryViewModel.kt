package creations.rimov.com.chipit.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.activities.MainActivity
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipTopic
import creations.rimov.com.chipit.database.objects.TopicAndChildren
import creations.rimov.com.chipit.database.repos.DirectoryRepository
import creations.rimov.com.chipit.objects.ViewModelPrompts

class DirectoryViewModel : ViewModel(), DirectoryRepository.DirRepoHandler {

    //TODO NOW: have to send the DirHandler below, don't know how my head hurts
    private val topicRepo: DirectoryRepository = DirectoryRepository(DatabaseApplication.database!!, this)

    private val topics: MutableLiveData<List<TopicAndChildren>> = MutableLiveData()

    val prompts = MutableLiveData<ViewModelPrompts>()


    fun updateTopics() {
        topicRepo.setTopics()
    }
    /** Repository functions **/
    fun getTopics() = topics

    fun insertTopic(topic: Chip) {
        topicRepo.insert(topic)
    }

    fun updateTopicName(id: Long, name: String) {
        topicRepo.update(id, name, null)
    }

    fun updateTopicImage(id: Long, imgLocation: String) {
        topicRepo.update(id, "", imgLocation)
    }

    fun deleteTopic(id: Long) {
        topicRepo.deleteTopicAndChildren(id)
    }

    fun handleChipGesture(gesture: Int) {

        when(gesture) {

            MainActivity.Constants.GESTURE_UP -> {
                prompts.postValue(ViewModelPrompts(toNextScreen = true))
            }

            MainActivity.Constants.GESTURE_LONG_TOUCH -> {
                prompts.postValue(ViewModelPrompts(true))
            }
        }
    }

    override fun setTopics(topics: List<TopicAndChildren>) {
        this.topics.postValue(topics)
    }
}