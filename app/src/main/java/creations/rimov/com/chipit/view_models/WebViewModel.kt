package creations.rimov.com.chipit.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.activities.WebActivity
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.repos.TopicChipRepository

class WebViewModel : ViewModel() {

    private val topicChipRepo = TopicChipRepository(DatabaseApplication.database!!)

    private var hTopicId: Long = 0L

    private lateinit var chipsHorizontal: LiveData<List<Chip>>
    private lateinit var chipsVertical: LiveData<List<Chip>>


    fun initChips(hTopicId: Long) {
        this.hTopicId = hTopicId
        chipsHorizontal = topicChipRepo.getTopicChips(hTopicId)
    }

    fun getHorizontalTopicId() = hTopicId

    //TODO: (FUTURE) if return an empty list trigger a display saying list is empty
    fun getChipsHorizontal(): LiveData<List<Chip>>? = chipsHorizontal

    /**Insert child into horizontal chip row**/
    fun insertChipH(chip: Chip) {
        topicChipRepo.insertChild(chip)
    }

    fun getChipsVertical(): LiveData<List<Chip>>? = chipsVertical

    /**Return the chip in the specified listType at the specified position**/
    fun getChipAtPosition(listType: Int, position: Int) =
        if(listType == WebActivity.Constant.HORIZONTAL_CHIP_LIST)
            chipsHorizontal.value?.get(position)
        else
            null
}