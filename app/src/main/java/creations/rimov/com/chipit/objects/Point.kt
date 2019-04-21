package creations.rimov.com.chipit.objects

class Point(val x: Float, val y: Float) {

    override fun toString() = x.toString().plus(',').plus(y.toString())
}