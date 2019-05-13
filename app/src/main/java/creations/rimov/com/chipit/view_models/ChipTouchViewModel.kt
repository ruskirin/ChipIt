package creations.rimov.com.chipit.view_models

import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.objects.Point

class ChipTouchViewModel : ViewModel() {

    private object Constant {
        //Radius around path starting point which will autosnap an endpoint and complete the chip
        const val TOLERANCE = 70f
        //Minimum distance from a path vertex before another vertex is created and connected
        const val LINE_INTERVAL = 50f
        //Buffer zone around the edge to trigger touch events
        const val EDGE_TOUCH_BUFFER = 10f
        //Swipe distance to trigger event
        const val SWIPE_BUFFER = 150f
    }

    //Flag to toggle path creation panel
    var pathCreated = MutableLiveData(false)

    private lateinit var pathVertices: MutableList<Point>


    fun getPathVertices() = pathVertices

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
        if(pathVertices.size <= 3 || displacement > Constant.TOLERANCE)
            return false

        //Path will complete at the first point
        pathVertices.add(pathVertices.first())
        //Convert the pixel points to normalized
        pathVertices = Point.normalizeList(pathVertices, viewWidth, viewHeight, imageWidth, imageHeight)

        pathCreated.postValue(true)

        return true
    }

    private var yi = 0f

    fun gestureAction(event: Int, point: Point) {

        when(event) {
            MotionEvent.ACTION_DOWN -> {

                //TODO NOW: figure out how and where you'll differentiate between gesture events and path drawing
                if((/*screenH - */point.y) > Constant.EDGE_TOUCH_BUFFER)
                //needEdit.postValue(false)

                    yi = point.y
            }
            MotionEvent.ACTION_MOVE -> {

                //if((yi - point.y) > Constant.SWIPE_BUFFER)
                //needEdit.postValue(true)
            }
            MotionEvent.ACTION_UP -> {

                yi = 0f //Reset
            }
        }
    }
}