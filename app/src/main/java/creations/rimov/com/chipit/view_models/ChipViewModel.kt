package creations.rimov.com.chipit.view_models

import android.graphics.Bitmap
import android.util.Log
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

    private object Constant {
        //Radius around path starting point which will autosnap an endpoint and complete the chip
        const val TOLERANCE = 70f
        //Minimum distance from a path vertex before another vertex is created and connected
        const val LINE_INTERVAL = 50f
    }

    private val chipRepo = ChipRepository(DatabaseApplication.database!!, this)

    //The chip currently viewed in the background and being worked on
    private lateinit var parent: LiveData<Chip>
    //Children of the main chip
    private lateinit var children: LiveData<List<Chip>> //TODO: observe the children, notifydatasetchanged if change observed

    var parentId = -1L
    //id of a newly inserted chip, used to update chip information
    var newChipId = -1L

    private lateinit var bitmap: Bitmap
    //Saved image path for present bitmap
    private var imgLocation: String = ""

    private lateinit var pathVertices: MutableList<Point>

    //Flag to redraw background bitmap
    var backgroundChanged = false
    //Flag to toggle path creation panel
    var pathCreated = MutableLiveData(false)


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

    fun getPathVertices() = pathVertices

    fun updateChip(id: Long, name: String?, imgLocation: String?, vertices: List<Point>?) {
        chipRepo.updateChip(id, name, imgLocation, vertices)
    }

    fun saveChip(name: String?, imgLocation: String?, vertices: List<Point>?) {
        val chip = Chip(0, parentId, name, imgLocation ?: "", vertices)

        chipRepo.insertChip(chip)
    }


    fun startPath(point: Point) {
        Log.i("Path Creation", "#startPath(): point x = ${point.x}, y = ${point.y}")
        pathVertices = mutableListOf()
        //Save the starting point
        pathVertices.add(point)
    }

    /**
     * @return true if point is valid and should be drawn, false if invalid and should be ignored
     */
    fun dragPath(point: Point): Boolean {

        val distance = point.distanceTo(pathVertices.last())

        if(distance < Constant.LINE_INTERVAL)
            return false

        pathVertices.add(point)
        return true
    }

    fun endPath(point: Point, viewWidth: Int, viewHeight: Int, imageWidth: Int, imageHeight: Int): Boolean {

        val displacement = point.distanceTo(pathVertices.first())

        //Does the path return to its starting point?
        if(displacement < Constant.TOLERANCE) {
            //Path will complete at the first point
            pathVertices.add(pathVertices.first())
            //Convert the pixel points to normalized
            pathVertices = Point.normalizeList(pathVertices, viewWidth, viewHeight, imageWidth, imageHeight)

            pathCreated.postValue(true)

            return true
        }

        return false
    }

    //Method from ChipRepoCommunication interface
    override fun returnChipId(id: Long) {
        newChipId = id
    }
}