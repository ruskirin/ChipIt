package creations.rimov.com.chipit.util

import android.graphics.Rect
import creations.rimov.com.chipit.objects.Point
import kotlin.math.roundToInt

object RenderUtil {

    //Returns a Rect centered on the view, maintaining the image aspect ratio
    fun getAspectRatioRect(imageWidth: Int, imageHeight: Int,
                           viewWidth: Int, viewHeight: Int): Rect {

        val viewVertical = (viewWidth <= viewHeight)
        //New image dimensions adjusted according to screen dimensions
        val modifiedHeight = (imageHeight / imageWidth.toFloat()) * viewWidth
        val modifiedWidth = (imageWidth / imageHeight.toFloat()) * viewHeight

        if(viewVertical) {
            //Image doesn't fit horizontally
            return if(modifiedWidth > viewWidth) {
                val offsetY = ((viewHeight - modifiedHeight) / 2).roundToInt()

                Rect(0, offsetY, viewWidth, viewHeight - offsetY)

            } else {
                val offsetX = ((viewWidth - modifiedWidth) / 2).roundToInt()

                Rect(offsetX, 0, viewWidth - offsetX, viewHeight)
            }

        } else {
            //Image doesn't fit vertically
            return if(modifiedHeight > viewHeight) {
                val offsetX = ((viewWidth - modifiedWidth) / 2).roundToInt()

                Rect(offsetX, 0, viewWidth - offsetX, viewHeight)

            } else {
                val offsetY = ((viewHeight - modifiedHeight) / 2).roundToInt()

                Rect(0, offsetY, viewWidth, viewHeight - offsetY)
            }
        }
    }

    fun pointNormToPx(point: Point, 
                      viewWidth: Int, viewHeight: Int, 
                      imageWidth: Int, imageHeight: Int): Point {

        //Length of the surrounding empty space
        val widthOffset = (viewWidth - imageWidth) / 2
        val heightOffset = (viewHeight - imageHeight) / 2
        //Inverse process of normalization
        val pX = ((point.x + 1) * (imageWidth / 2)) + widthOffset
        val pY = ((point.y + 1) * (imageHeight / 2)) + heightOffset

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
     * Coordinates in range [-1,1], unlike actual normalized coordinates y-axis is flipped (-1 on top, 1 bottom) for convenience.
     * Normalized with respect to imageWidth and imageHeight, as the placement of vertices matters relative to background image.
     */
    fun pointToNorm(point: Point,
                    viewWidth: Int, viewHeight: Int,
                    imageWidth: Int, imageHeight: Int): Point {

        //Subtract the empty borders surrounding the image
        val pointX = point.x - ((viewWidth - imageWidth) / 2)
        val pointY = point.y - ((viewHeight - imageHeight) / 2)

        val nX = ((pointX * 2) / imageWidth) - 1f
        val nY = ((pointY * 2) / imageHeight) - 1f

        return Point(nX, nY)
    }

    fun listPxToNorm(points: List<Point>,
                     viewWidth: Int, viewHeight: Int,
                     imageWidth: Int, imageHeight: Int): MutableList<Point> {

        val list = mutableListOf<Point>()

        points.forEach {
            list.add(pointToNorm(it, viewWidth, viewHeight, imageWidth, imageHeight))
        }

        return list
    }
}