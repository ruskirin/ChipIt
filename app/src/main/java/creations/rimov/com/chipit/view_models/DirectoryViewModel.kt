package creations.rimov.com.chipit.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.activities.DirectoryActivity
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.repos.TopicChipRepository
import creations.rimov.com.chipit.objects.ViewModelPrompts

class DirectoryViewModel : ViewModel() {

    private val topicRepo: TopicChipRepository = TopicChipRepository(DatabaseApplication.database!!)

    private val topics: LiveData<List<Chip>> = topicRepo.getTopics()

    val prompts = MutableLiveData<ViewModelPrompts>()


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


    var chipTouchPos: Int = -1
    var chipTouchId: Long = -1L

    fun handleChipGesture(gesture: Int) {

        when(gesture) {
            DirectoryActivity.Constants.GESTURE_UP -> {

                prompts.postValue(ViewModelPrompts(false, true))
            }
            DirectoryActivity.Constants.GESTURE_LONG_TOUCH -> {


            }
        }
    }

    fun resetFlags() {
        prompts.postValue(ViewModelPrompts())
    }
}