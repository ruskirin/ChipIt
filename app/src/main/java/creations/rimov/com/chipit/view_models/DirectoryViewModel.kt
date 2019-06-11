package creations.rimov.com.chipit.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.activities.DirectoryActivity
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.repos.DirectoryRepository
import creations.rimov.com.chipit.objects.ViewModelPrompts

class DirectoryViewModel : ViewModel() {

    private val topicRepo: DirectoryRepository = DirectoryRepository(DatabaseApplication.database!!)

    private val topics: LiveData<List<ChipCard>> = topicRepo.getTopics()

    val prompts = MutableLiveData<ViewModelPrompts>()


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

    var chipTouchId: Long = -1L

    fun handleChipGesture(gesture: Int) {

        when(gesture) {

            DirectoryActivity.Constants.GESTURE_UP -> {
                prompts.postValue(ViewModelPrompts(selectChip = true))
            }

            DirectoryActivity.Constants.GESTURE_DOUBLE_TAP -> {
                prompts.postValue(ViewModelPrompts(toNextScreen = true))
            }

            DirectoryActivity.Constants.GESTURE_LONG_TOUCH -> {
                prompts.postValue(ViewModelPrompts(true))
            }
        }
    }
}