package creations.rimov.com.chipit.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipAndChildren
import creations.rimov.com.chipit.database.repos.TopicChipRepository

class WebViewModel : ViewModel() {

    private val topicChipRepo = TopicChipRepository(DatabaseApplication.database!!)

    private var topicId: Long = 0L

    private lateinit var chipsHorizontal: LiveData<List<Chip>>
    private lateinit var chipsVertical: LiveData<List<Chip>>


    fun initChips(topicId: Long) {
        this.topicId = topicId
        chipsHorizontal = topicChipRepo.getTopicChips(topicId)
    }

    //TODO: (FUTURE) if return an empty list trigger a display saying list is empty
    fun getChipsHorizontal(): LiveData<List<Chip>>? = chipsHorizontal

    fun getChipsVertical(): LiveData<List<Chip>>? = chipsVertical

    fun insertChipH(chip: Chip) {

        chip.parentId = topicId
        topicChipRepo.insertChild(chip)
    }
}