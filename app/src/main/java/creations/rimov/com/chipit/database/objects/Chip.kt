package creations.rimov.com.chipit.database.objects

import androidx.room.*
import creations.rimov.com.chipit.objects.Point
import creations.rimov.com.chipit.util.RenderUtil

@Entity(
    tableName = "chips",
    indices = [Index(value = ["id"], unique = true)]
)

data class Chip(

    @PrimaryKey(autoGenerate = true) val id: Long,

    @ColumnInfo(name = "parent_id") var parentId: Long,

    var name: String?,

    @ColumnInfo(name = "image_path") var imagePath: String,

    var vertices: MutableList<Point>?) {


    /** Return a float array version of vertices
     * @param drawing: make 2 copies of every value in the list except the first and last to allow drawing of continous shapes
     *                  eg. vertices (a,b,c,d) will connect (a,b), (b,c), (c,d)
     * @param pixelize: by default vertices are normalized. Specify width, height, imageWidth, and imageHeight to pixelize
     */
    fun getVerticesFloatArray(drawing: Boolean, pixelize: Boolean,
                              width: Int = 0, height: Int = 0,
                              imageWidth: Int = 0, imageHeight: Int = 0): FloatArray {

        if(vertices != null) {

            val verticesF = FloatArray(
                if (drawing) {
                    (vertices!!.size - 2) * 4 + 4 //all but end elements are doubled, each one has 2 components + 2 * 2 end components
                } else {
                    vertices!!.size * 2
                }
            )

            var idx = 0

            if (pixelize) {
                if (width == 0 || height == 0 || imageWidth == 0 || imageHeight == 0)
                    return FloatArray(0)

                val list = RenderUtil.listNormToPx(vertices!!, width, height, imageWidth, imageHeight)

                if (drawing) {

                    list.forEachIndexed { i, point ->
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

                } else {
                    list.forEach {
                        verticesF[idx] = it.x
                        verticesF[++idx] = it.y
                        ++idx
                    }
                }

            } else {
                vertices!!.forEach {
                    verticesF[idx] = it.x
                    verticesF[++idx] = it.y
                    ++idx
                }
            }

            return verticesF
        }

        return floatArrayOf()
    }
}
