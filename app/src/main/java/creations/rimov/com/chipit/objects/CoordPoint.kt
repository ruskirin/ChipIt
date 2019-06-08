package creations.rimov.com.chipit.objects

import kotlin.math.sqrt

class CoordPoint(val x: Float, val y: Float) {

    companion object {

        @JvmStatic
        fun normalizeList(vertices: List<CoordPoint>,
                          viewWidth: Int, viewHeight: Int,
                          imageWidth: Int, imageHeight: Int): MutableList<CoordPoint> {

            val converted = mutableListOf<CoordPoint>()

            vertices.forEach {
                converted.add(it.normalize(viewWidth, viewHeight, imageWidth, imageHeight))
            }

            return converted
        }

        @JvmStatic
        fun pixelizeList(vertices: List<CoordPoint>,
                         viewWidth: Int, viewHeight: Int,
                         imageWidth: Int, imageHeight: Int): MutableList<CoordPoint> {

            val converted = mutableListOf<CoordPoint>()

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

    fun distanceTo(coordPoint: CoordPoint): Float = sqrt((coordPoint.x - x)*(coordPoint.x - x) + (coordPoint.y - y)*(coordPoint.y - y))

    /**
     * @see NOTE unlike regular normalization, top left of screen is considered (-1, -1)
     * @return point with x and y values ranging [-1, 1]
     */
    fun normalize(viewWidth: Int, viewHeight: Int,
                     imageWidth: Int, imageHeight: Int): CoordPoint {

        //Subtract the empty borders surrounding the image
        val nX = x - ((viewWidth - imageWidth) / 2)
        val nY = y - ((viewHeight - imageHeight) / 2)

        return CoordPoint(
            ((nX * 2) / imageWidth) - 1f,
            ((nY * 2) / imageHeight) - 1f)
    }

    /**
     * @param imageWidth, imageHeight: scaled dimensions of bitmap to be drawn on
     * @return point with (x,y) scaled to view size
     */
    fun pixelize(viewWidth: Int, viewHeight: Int,
                 imageWidth: Int, imageHeight: Int): CoordPoint {

        //Difference between start of image and view border
        val dx = (viewWidth - imageWidth) / 2
        val dy = (viewHeight - imageHeight) / 2

        //Inverse of normalization transformation
        return CoordPoint(
            ((x + 1) * (imageWidth / 2)) + dx,
            ((y + 1) * (imageHeight / 2)) + dy)
    }
}