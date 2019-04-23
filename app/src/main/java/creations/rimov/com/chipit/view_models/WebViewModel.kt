package creations.rimov.com.chipit.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipAndChildren
import creations.rimov.com.chipit.database.objects.TopicAndChips
import creations.rimov.com.chipit.database.repos.TopicChipRepository

class WebViewModel : ViewModel() {

    private val topicChipRepo = TopicChipRepository(DatabaseApplication.database!!)

    private lateinit var chipsHorizontal: LiveData<List<Chip>>
    private lateinit var chipsVertical: LiveData<List<Chip>>


    fun initChips(topicId: Long) {
        chipsHorizontal = topicChipRepo.getTopicAndChips(topicId).children
    }

    //TODO: (FUTURE) return an empty list which triggers a display saying list is empty
    fun getChipsHorizontal(): LiveData<List<Chip>>? =
        if(::chipsHorizontal.isInitialized)
            chipsHorizontal
        else
            null
}