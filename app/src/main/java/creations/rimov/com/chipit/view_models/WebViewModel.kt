package creations.rimov.com.chipit.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.activities.WebActivity
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.repos.TopicChipRepository

class WebViewModel : ViewModel() {

    private val topicChipRepo = TopicChipRepository(DatabaseApplication.database!!)

    private var parentId: Long = 0L

    private lateinit var chipsHorizontal: LiveData<List<ChipCard>>
    private lateinit var chipsVertical: LiveData<List<ChipCard>>


    fun initChips(parentId: Long) {
        this.parentId = parentId
        chipsHorizontal = topicChipRepo.getTopicChipCards(parentId)
    }

    //TODO (NOW): change the layout of the web, then work on registering the middle item in the recyclerview
    //             for caching of content for the bottom recyclerview

    fun getParentId() = parentId

    //TODO: (FUTURE) if return an empty list trigger a display saying list is empty
    fun getChipsHorizontal(): LiveData<List<ChipCard>>? =
        if(::chipsHorizontal.isInitialized)
            chipsHorizontal
        else
            null

    fun getChipsVertical(): LiveData<List<ChipCard>>? =
        if(::chipsVertical.isInitialized)
            chipsVertical
        else
            null

    /**Insert child into horizontal chip row**/
    fun saveChip(name: String?, imgLocation: String?) {
        val chip = Chip(0, parentId, name, imgLocation ?: "", null)

        topicChipRepo.insertChip(chip)
    }

    /**Return the chip in the specified listType at the specified position**/
    fun getChipAtPosition(listType: Int, position: Int) =
        if(listType == WebActivity.Constant.HORIZONTAL_CHIP_LIST)
            chipsHorizontal.value?.get(position)
        else
            null
}