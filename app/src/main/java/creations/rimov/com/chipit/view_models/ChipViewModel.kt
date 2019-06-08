package creations.rimov.com.chipit.view_models

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.objects.ChipPath
import creations.rimov.com.chipit.database.repos.ChipRepository
import creations.rimov.com.chipit.objects.CoordPoint
import creations.rimov.com.chipit.util.TextureUtil
import java.lang.Exception

class ChipViewModel : ViewModel(), ChipRepository.ChipRepoCommunication {

    private val chipRepo = ChipRepository(DatabaseApplication.database!!, this)

    //Is the parent of the current parent a topic? Used to toggle branch up navigation
    private var parentOfParentIsTopic: Boolean = false
    //The chip currently viewed in the background and being worked on
    private val parent: MutableLiveData<ChipIdentity> = MutableLiveData()
    //Children of the main chip
    private lateinit var children: LiveData<List<ChipPath>>

    var parentId = -1L
    //id of a newly inserted chip, used to update chip information
    var newChipId = -1L

    private lateinit var bitmap: Bitmap
    //Saved image path for present bitmap
    private var imgLocation: String = ""

    //Flag to redraw background bitmap
    var backgroundChanged = false


    fun setParent(parentId: Long) {

        if(parentId != this.parentId) {
            this.parentId = parentId

            chipRepo.setParentIdentity(parentId)
            children = chipRepo.getChildren(parentId)
        }
    }

    fun getParent(): MutableLiveData<ChipIdentity>? = parent

    fun getParentIdOfParent() = parent.value?.parentId ?: -1L

    fun isParentOfParentTopic() = parentOfParentIsTopic

    fun getChildren(): LiveData<List<ChipPath>>? =
        if(::children.isInitialized) children
        else null

    /**Check if point landed inside one of the children**/
    fun getTouchedChip(point: CoordPoint): ChipPath? {

        children.value?.forEach { chip ->

            if(chip.isInside(point)) {
                return chip
            }
        }

        return null
    }

    fun setBitmap(imgLocation: String) {
        this.imgLocation = imgLocation

        try {
            bitmap = TextureUtil.convertPathToBitmap(imgLocation)!!

        } catch(e: Exception) {
            Log.e("ChipViewModel", "#setBitmap(): could not create bitmap from passed image path!")
            e.printStackTrace()

        } finally {
            backgroundChanged = true
        }
    }

    fun getBitmap() =
        if(::bitmap.isInitialized) bitmap
        else null

    fun getBitmapWidth() =
        if(::bitmap.isInitialized) bitmap.width
        else 0

    fun getBitmapHeight() =
        if(::bitmap.isInitialized) bitmap.height
        else 0

    fun updateChip(id: Long,
                   name: String? = null,
                   imgLocation: String? = null,
                   vertices: List<CoordPoint>? = null) {

        chipRepo.updateChip(id, name, imgLocation, vertices)
    }

    fun saveChip(name: String?, imgLocation: String?, vertices: List<CoordPoint>?) {
        val chip = Chip(0, parentId, false, name, imgLocation ?: "", vertices)

        Log.i("Chip Creation", "ChipViewModel#saveChip(): saving chip under parent $parentId")

        chipRepo.insertChip(chip)
    }

    //Method from ChipRepoCommunication interface
    override fun setChipId(id: Long) {
        newChipId = id
    }

    override fun updateParent(parent: ChipIdentity) {
        this.parent.postValue(parent)

        chipRepo.isChipTopic(parent.parentId)
    }

    override fun isParentTopic(isTopic: Boolean) {
        this.parentOfParentIsTopic = isTopic
    }
}