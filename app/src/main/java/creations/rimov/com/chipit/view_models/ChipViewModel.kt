package creations.rimov.com.chipit.view_models

import android.graphics.Bitmap
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.repos.ChipRepository
import creations.rimov.com.chipit.objects.Point
import creations.rimov.com.chipit.util.TextureUtil
import java.lang.Exception

class ChipViewModel : ViewModel(), ChipRepository.ChipRepoCommunication {

    private val chipRepo = ChipRepository(DatabaseApplication.database!!, this)

    //The chip currently viewed in the background and being worked on
    private lateinit var parent: LiveData<Chip>
    //Children of the main chip
    private lateinit var children: LiveData<List<Chip>>

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

            parent = chipRepo.getChip(parentId)
            children = chipRepo.getChildren(parentId)
        }
    }

    fun getParent(): LiveData<Chip>? =
        if(isParentInit()) parent
        else null

    fun isParentInit() = ::parent.isInitialized

    fun getChildren(): LiveData<List<Chip>> = children

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

    fun getImagePath() = imgLocation

    fun updateChip(id: Long, name: String?, imgLocation: String?, vertices: List<Point>?) {
        chipRepo.updateChip(id, name, imgLocation, vertices)
    }

    fun saveChip(name: String?, imgLocation: String?, vertices: List<Point>?) {
        val chip = Chip(0, parentId, name, imgLocation ?: "", vertices)

        chipRepo.insertChip(chip)
    }

    //Method from ChipRepoCommunication interface
    override fun returnChipId(id: Long) {
        newChipId = id
    }
}