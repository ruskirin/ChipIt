package creations.rimov.com.chipit.view_models

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.objects.CoordPoint

class ChipperTouchViewModel : ViewModel() {

    private object Constant {
        //Radius around path starting point which will autosnap an endpoint and complete the chip
        const val TOLERANCE = 70f
        //Minimum distance from a path vertex before another vertex is created and connected
        const val LINE_INTERVAL = 50f
    }

    private val path: Path = Path()
    private val paint: Paint = Paint()
    //Flag to toggle path creation panel
    val pathCreated: MutableLiveData<Boolean> = MutableLiveData(false)

    private lateinit var pathVertices: MutableList<CoordPoint>


    fun getDrawPath() = path

    fun getDrawPaint() = paint

    fun getPathVertices() =
        if(::pathVertices.isInitialized) pathVertices
        else null

    fun startPath(point: CoordPoint) {

        Log.i("Path Creation", "#startPath(): point x = ${point.x}, y = ${point.y}")

        path.moveTo(point.x, point.y)

        pathVertices = mutableListOf()
        //Save the starting point
        pathVertices.add(point)
    }

    fun dragPath(point: CoordPoint) {

        val distance = point.distanceTo(pathVertices.last())

        if(distance < Constant.LINE_INTERVAL) return

        path.lineTo(point.x, point.y)

        pathVertices.add(point)
    }

    fun endPath(point: CoordPoint, viewWidth: Int, viewHeight: Int, imageWidth: Int, imageHeight: Int) {

        val displacement = point.distanceTo(pathVertices.first())

        //Does the path return to its starting point?
        if(pathVertices.size <= 3 || displacement > Constant.TOLERANCE) {
            //Either path has been saved or was incomplete, regardless it is no longer necessary
            clearPaths(path)
            return
        }

        path.lineTo(point.x, point.y)
        //Path will complete at the first point
        pathVertices.add(pathVertices.first())
        //Convert the pixel points to normalized
        pathVertices = CoordPoint.normalizeList(pathVertices, viewWidth, viewHeight, imageWidth, imageHeight)

        pathCreated.postValue(true)

        //Either path has been saved or was incomplete, regardless it is no longer necessary
        clearPaths(path)
    }

    fun initPaint() {

        paint.apply {
            isAntiAlias = true
            color = Color.CYAN
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeWidth = 4f
        }
    }

    /**
     * Reset path objects
     */
    private fun clearPaths(vararg paths: Path) {
        paths.forEach {
            it.reset()
        }
    }
}