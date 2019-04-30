package creations.rimov.com.chipit.objects

import kotlin.math.sqrt

class Point(val x: Float, val y: Float) {

    override fun toString() = x.toString().plus(',').plus(y.toString())

    fun distanceTo(point: Point) = sqrt((x + point.x) + (y + point.y))
}