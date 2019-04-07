package creations.rimov.com.chipit.util

import android.graphics.Rect
import creations.rimov.com.chipit.objects.Point
import kotlin.math.roundToInt

object RenderUtil {

    //Returns a Rect centered on the view, maintaining the image aspect ratio
    fun getAspectRatioRect(imageW: Int, imageH: Int, viewW: Int, viewH: Int): Rect {

        val viewVertical = (viewW <= viewH)
        //New image dimensions adjusted according to screen dimensions
        val modifiedHeight = (imageH.toFloat() / imageW.toFloat()) * viewW
        val modifiedWidth = (imageW.toFloat() / imageH.toFloat()) * viewH

        if(viewVertical) {
            //Image doesn't fit horizontally
            return if(modifiedWidth > viewW) {
                val offsetY = ((viewH - modifiedHeight) / 2).roundToInt()

                Rect(0, offsetY, viewW, viewH - offsetY)

            } else {
                val offsetX = ((viewW - modifiedWidth) / 2).roundToInt()

                Rect(offsetX, 0, viewW - offsetX, viewH)
            }

        } else {
            //Image doesn't fit vertically
            return if(modifiedHeight > viewH) {
                val offsetX = ((viewW - modifiedWidth) / 2).roundToInt()

                Rect(offsetX, 0, viewW - offsetX, viewH)

            } else {
                val offsetY = ((viewH - modifiedHeight) / 2).roundToInt()

                Rect(0, offsetY, viewW, viewH - offsetY)
            }
        }
    }

    fun pointNormToPx(point: Point, viewWidth: Int, viewHeight: Int, imageWidth: Int, imageHeight: Int): Point {

        val widthOffset = (viewWidth - imageWidth).toFloat() / 2
        val heightOffset = (viewHeight - imageHeight).toFloat() / 2

        val pX = (((point.x + 1) * imageWidth.toFloat()) / 2) + widthOffset
        val pY = (((point.y + 1) * imageHeight.toFloat()) / 2) + heightOffset

        return Point(pX, pY)
    }

    fun listNormToPx(points: List<Point>,
                     viewWidth: Int, viewHeight: Int,
                     imageWidth: Int, imageHeight: Int): List<Point> {

        val list = mutableListOf<Point>()

        points.forEach {
            list.add(pointNormToPx(it, viewWidth, viewHeight, imageWidth, imageHeight))
        }

        return list
    }

    /**
     * Coordinates in range [-1,1], unlike actual normalized coordinates y-axis is flipped (-1 on top, 1 bottom) for convenience
     */
    fun pointToNorm(point: Point, width: Int, height: Int): Point {
        val nX = ((point.x * 2) / width.toFloat()) - 1f
        val nY = ((point.y * 2) / height.toFloat()) - 1f

        return Point(nX, nY)
    }

    fun listPxToNorm(points: List<Point>, width: Int, height: Int): List<Point> {
        val list = mutableListOf<Point>()

        points.forEach {
            list.add(pointToNorm(it, width, height))
        }

        return list
    }
}