package creations.rimov.com.chipit.database.objects

import android.util.Log
import androidx.room.ColumnInfo
import creations.rimov.com.chipit.objects.CoordPoint

data class ChipPath(
    val id: Long,
    @ColumnInfo(name = "image_location") val imgLocation: String,
    val vertices: List<CoordPoint>?) {

    /**
     * @param drawing: make 2 copies of every value in the list except the first and last to allow drawing of continous shapes
     *                  eg. vertices (a,b,c,d) will connect (a,b), (b,c), (c,d), (d,a)
     * @return pixelized FloatArray of vertices
     */
    fun getVerticesFloatArray(drawing: Boolean,
                              viewWidth: Int, viewHeight: Int,
                              imageWidth: Int, imageHeight: Int): FloatArray? {

        if(vertices == null || vertices.size <= 3) {
            return null
        }

        if(viewWidth == 0 || viewHeight == 0 || imageWidth == 0 || imageHeight == 0) {

            Log.e("Chip.kt", "#getVerticesFloatArray(): invalid dimensions passed!")
            return null
        }

        var idx = 0

        val verticesF = FloatArray(
            if (drawing) {
                (vertices.size - 2) * 4 + 4 //all but end elements are doubled, each one has 2 components + 2 * 2 end components
            } else {
                vertices.size * 2
            }
        )

        val list = CoordPoint.pixelizeList(vertices, viewWidth, viewHeight, imageWidth, imageHeight)

        if(!drawing) {
            list.forEach { point ->
                verticesF[idx] = point.x
                verticesF[++idx] = point.y
                ++idx
            }

            return verticesF
        }

        list.forEachIndexed { i, point ->
            //if (first vertex OR last vertex then only create 1 copy)
            if (i == 0 || i == list.lastIndex) {
                verticesF[idx] = point.x
                verticesF[++idx] = point.y
                ++idx

                return@forEachIndexed
            }

            verticesF[idx] = point.x
            verticesF[++idx] = point.y
            verticesF[++idx] = point.x
            verticesF[++idx] = point.y
            ++idx
        }

        return verticesF
    }

    /**Is point0 inside the area defined by vertices?**/
    fun isInside(point0: CoordPoint): Boolean {
        //Identify closest and furthest points from point0
        val closest: CoordPoint = getClosestPoint(point0) ?: return false
        val furthest: CoordPoint = getFurthestPoint(closest) ?: return false

        if(furthest.distanceTo(point0) < furthest.distanceTo(closest))
            return true

        return false
    }

    /**Return point in vertices closest to point0**/
    private fun getClosestPoint(point0: CoordPoint): CoordPoint? {
        var point: CoordPoint? = null
        var distance: Float = 2f

        vertices?.forEach { coord ->
            val d = point0.distanceTo(coord)

            if(d <= distance) {
                point = coord
                distance = d
            }
        }

        return point
    }

    /**Return point in vertices farthest (approximately) from point0**/
    private fun getFurthestPoint(point0: CoordPoint): CoordPoint? {

        if(vertices == null)
            return null

        val distance: Int = vertices.size / 2
        val location: Int = vertices.indexOf(point0)

        if((location + distance) >= vertices.size)
            return vertices[location - distance]

        return vertices[location + distance]
    }
}