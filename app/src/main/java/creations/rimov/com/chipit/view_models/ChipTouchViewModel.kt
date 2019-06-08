package creations.rimov.com.chipit.view_models

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.objects.CoordPoint

class ChipTouchViewModel : ViewModel() {

    private object Constant {
        //Radius around path starting point which will autosnap an endpoint and complete the chip
        const val TOLERANCE = 70f
        //Minimum distance from a path vertex before another vertex is created and connected
        const val LINE_INTERVAL = 50f
    }

    private val path: Path = Path()
    private val paint: Paint = Paint()
    //Flag to toggle path creation panel
    var pathCreated = MutableLiveData(false)

    private lateinit var pathVertices: MutableList<CoordPoint>


    fun getDrawPath() = path

    fun getDrawPaint() = paint

    fun getPathVertices() = pathVertices

    fun startPath(coordPoint: CoordPoint) {

        Log.i("Path Creation", "#startPath(): coordPoint x = ${coordPoint.x}, y = ${coordPoint.y}")

        path.moveTo(coordPoint.x, coordPoint.y)

        pathVertices = mutableListOf()
        //Save the starting coordPoint
        pathVertices.add(coordPoint)
    }

    fun dragPath(coordPoint: CoordPoint) {

        val distance = coordPoint.distanceTo(pathVertices.last())

        if(distance < Constant.LINE_INTERVAL)
            return

        path.lineTo(coordPoint.x, coordPoint.y)

        pathVertices.add(coordPoint)
    }

    fun endPath(coordPoint: CoordPoint, viewWidth: Int, viewHeight: Int, imageWidth: Int, imageHeight: Int) {

        val displacement = coordPoint.distanceTo(pathVertices.first())

        //Does the path return to its starting coordPoint?
        if(pathVertices.size <= 3 || displacement > Constant.TOLERANCE) {
            //Either path has been saved or was incomplete, regardless it is no longer necessary
            clearPaths(path)
            return
        }

        path.lineTo(coordPoint.x, coordPoint.y)
        //Path will complete at the first coordPoint
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