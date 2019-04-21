package creations.rimov.com.chipit.database

import androidx.room.TypeConverter
import creations.rimov.com.chipit.objects.Point

object DbVertexConverter {

    @TypeConverter
    @JvmStatic
    fun verticesToString(vertices: List<Point>): String? {
        if(vertices.isEmpty()) return null

        return vertices.joinToString(",") {
            it.toString()
        }
    }

    @TypeConverter
    @JvmStatic
    fun stringToVertices(vertices: String): MutableList<Point>? {
        if(vertices.isBlank()) return null

        val list = vertices.split(",")

        val points = mutableListOf<Point>()

        for(i in list.indices step 2) {
            points.add(Point(list[i].toFloat(), list[i+1].toFloat()))
        }

        return points
    }
}