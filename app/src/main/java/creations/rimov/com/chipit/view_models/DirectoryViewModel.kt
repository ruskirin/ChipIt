package creations.rimov.com.chipit.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.activities.MainActivity
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipTopic
import creations.rimov.com.chipit.database.objects.TopicAndChildren
import creations.rimov.com.chipit.database.repos.AccessRepo
import creations.rimov.com.chipit.objects.ViewModelPrompts

class DirectoryViewModel : ViewModel(), AccessRepo.RepoHandler {

    private val repository = AccessRepo(DatabaseApplication.database!!, this)

    private val topics: MutableLiveData<List<TopicAndChildren>> = MutableLiveData()

    val prompts = MutableLiveData<ViewModelPrompts>()


    fun getTopics() = topics

    fun updateTopics() {
        repository.setTopics()
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

    override fun <T> setData(data: T) {
        this.topics.postValue(data as List<TopicAndChildren>)
    }
}