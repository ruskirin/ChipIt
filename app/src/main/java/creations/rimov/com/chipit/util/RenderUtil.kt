package creations.rimov.com.chipit.util

import android.graphics.Rect
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
}