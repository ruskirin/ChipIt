package creations.rimov.com.chipit.data

import androidx.room.TypeConverter
import creations.rimov.com.chipit.util.objects.CoordPoint

object DbVertexConverter {

    @TypeConverter
    @JvmStatic
    fun verticesToString(vertices: List<CoordPoint>?): String? {
        if(vertices.isNullOrEmpty()) return null

        return vertices.joinToString(",") {
            it.toString()
        }
    }

    @TypeConverter
    @JvmStatic
    fun stringToVertices(vertices: String?): MutableList<CoordPoint>? {
        if(vertices.isNullOrBlank()) return null

        val list = vertices.split(",")

        val points = mutableListOf<CoordPoint>()

        for(i in list.indices step 2) {
            points.add(CoordPoint(list[i].toFloat(), list[i+1].toFloat()))
        }

        return points
    }
}