package creations.rimov.com.chipit.database.objects

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import creations.rimov.com.chipit.objects.Point

@Entity(
    tableName = "chips",
    indices = [Index(value = ["id"], unique = true)]
)

data class Chip(

    @PrimaryKey(autoGenerate = true) val id: Long,

    @ColumnInfo(name = "parent_id") val parentId: Long,

    var name: String?,

    @ColumnInfo(name = "image_location") var imgLocation: String,

    var vertices: List<Point>? = mutableListOf()) {


    /**
     * @param drawing: make 2 copies of every value in the list except the first and last to allow drawing of continous shapes
     *                  eg. vertices (a,b,c,d) will connect (a,b), (b,c), (c,d), (d,a)
     * @return pixelized FloatArray of vertices
     */
    fun getVerticesFloatArray(drawing: Boolean,
                              viewWidth: Int, viewHeight: Int,
                              imageWidth: Int, imageHeight: Int): FloatArray? {

        if(vertices!!.size <= 3) {
            return null
        }

        if(viewWidth == 0 || viewHeight == 0 || imageWidth == 0 || imageHeight == 0) {

            Log.e("Chip.kt", "#getVerticesFloatArray(): invalid dimensions passed!")
            return null
        }

        var idx = 0

        val verticesF = FloatArray(
            if (drawing) {
                (vertices!!.size - 2) * 4 + 4 //all but end elements are doubled, each one has 2 components + 2 * 2 end components
            } else {
                vertices!!.size * 2
            }
        )

        val list = Point.pixelizeList(vertices!!, viewWidth, viewHeight, imageWidth, imageHeight)

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
}
