package creations.rimov.com.chipit.view_models

import android.graphics.Bitmap
import android.graphics.Path
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.repos.ChipRepository
import creations.rimov.com.chipit.objects.Point
import creations.rimov.com.chipit.util.RenderUtil
import creations.rimov.com.chipit.util.TextureUtil
import java.lang.Exception

class ChipViewModel : ViewModel() {

    private object Constant {
        //Radius around path starting point which will autosnap an endpoint and complete the chip
        const val TOLERANCE = 100f
        //Minimum distance from a path vertex before another vertex is created and connected
        const val LINE_INTERVAL = 50f
    }

    private val chipRepo = ChipRepository(DatabaseApplication.database!!)

    var resetParent = MutableLiveData(false)

    var parentId = -1L

    //The chip currently viewed in the background and being worked on
    private lateinit var parent: LiveData<Chip>
    //Children of the main chip
    private lateinit var children: LiveData<List<Chip>> //TODO: observe the children, notifydatasetchanged if change observed

    private lateinit var bitmap: Bitmap

    private lateinit var pathVertices: MutableList<Point>


    fun setParent(parentId: Long) {

        if(parentId != this.parentId) {
            parent = chipRepo.getChip(parentId)
            this.parentId = parentId
        }
    }

    fun getParent(): LiveData<Chip> = parent

    fun getChildren() = chipRepo.getChildren(parentId)

    fun setBitmap(imagePath: String) {

        try {
            bitmap = TextureUtil.convertPathToBitmap(imagePath)!!

        } catch(e: Exception) {
            //TODO: handle the exception from null return
            Log.e("ChipViewModel", "#setChips: could not set background Bitmap")
            e.printStackTrace()
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

    fun startPath(point: Point) {
        pathVertices = mutableListOf(point)
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

    fun endPath(point: Point, width: Int, height: Int, frameWidth: Int, frameHeight: Int) {

        val displacement = point.distanceTo(pathVertices.first())

        //Does the path return to its starting point?
        if(displacement < Constant.TOLERANCE) {
            //Path will complete at the first point
            pathVertices.add(pathVertices.first())
            //Convert the pixel points to normalized
            pathVertices = RenderUtil.listPxToNorm(pathVertices, width, height, frameWidth, frameHeight)

            chipRepo.insertChip(
                Chip(0, parent.value!!.id, "", "", pathVertices))
        }
    }
}