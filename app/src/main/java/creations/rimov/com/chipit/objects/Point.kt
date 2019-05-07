package creations.rimov.com.chipit.objects

import kotlin.math.sqrt

class Point(val x: Float, val y: Float) {

    companion object {

        @JvmStatic
        fun normalizeList(vertices: List<Point>,
                          viewWidth: Int, viewHeight: Int,
                          imageWidth: Int, imageHeight: Int): MutableList<Point> {

            val converted = mutableListOf<Point>()

            vertices.forEach {
                converted.add(it.normalize(viewWidth, viewHeight, imageWidth, imageHeight))
            }

            return converted
        }

        @JvmStatic
        fun pixelizeList(vertices: List<Point>,
                         viewWidth: Int, viewHeight: Int,
                         imageWidth: Int, imageHeight: Int): MutableList<Point> {

            val converted = mutableListOf<Point>()

            vertices.forEach {
                converted.add(it.pixelize(viewWidth, viewHeight, imageWidth, imageHeight))
            }

            return converted
        }
    }

    /**
     * @see NOTE used for storage in database table
     */
    override fun toString() = x.toString().plus(',').plus(y.toString())

    fun distanceTo(point: Point): Float = sqrt((point.x - x)*(point.x - x) + (point.y - y)*(point.y - y))

    /**
     * @see NOTE unlike regular normalization, top left of screen is considered (-1, -1)
     * @return point with x and y values ranging [-1, 1]
     */
    fun normalize(viewWidth: Int, viewHeight: Int,
                     imageWidth: Int, imageHeight: Int): Point {

        //Subtract the empty borders surrounding the image
        val nX = x - ((viewWidth - imageWidth) / 2)
        val nY = y - ((viewHeight - imageHeight) / 2)

        return Point(
            ((nX * 2) / imageWidth) - 1f,
            ((nY * 2) / imageHeight) - 1f)
    }

    /**
     * @param imageWidth, imageHeight: scaled dimensions of bitmap to be drawn on
     * @return point with (x,y) scaled to view size
     */
    fun pixelize(viewWidth: Int, viewHeight: Int,
                 imageWidth: Int, imageHeight: Int): Point {

        //Difference between start of image and view border
        val dx = (viewWidth - imageWidth) / 2
        val dy = (viewHeight - imageHeight) / 2

        //Inverse of normalization transformation
        return Point(
            ((x + 1) * (imageWidth / 2)) + dx,
            ((y + 1) * (imageHeight / 2)) + dy)
    }
}