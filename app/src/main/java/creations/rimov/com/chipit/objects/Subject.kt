package creations.rimov.com.chipit.objects

import android.os.Parcel
import android.os.Parcelable
import creations.rimov.com.chipit.util.RenderUtil

class Subject(var imagePath: String = "") : Parcelable {

    var name: String = "DEFAULT"
    //Vertices of the mapped subject focus
    var vertices = mutableListOf<Point>() //TODO: deal with mapping to zoomed images
    //Children chips of this encompassing one
    var children = mutableListOf<Subject>()

    /** Return a float array version of vertices
     * @param drawing: make 2 copies of every value in the list except the first and last to allow drawing of continous shapes
     *                  eg. vertices (a,b,c,d) will connect (a,b), (b,c), (c,d)
     * @param pixelize: by default vertices are normalized. Specify width, height, imageWidth, and imageHeight to pixelize
     */
    fun getVerticesFloatArray(drawing: Boolean, pixelize: Boolean,
                              width: Int = 0, height: Int = 0,
                              imageWidth: Int = 0, imageHeight: Int = 0): FloatArray {

        val verticesF = FloatArray(
            if(drawing) {
                (vertices.size - 2) * 4 + 4 //all but end elements are doubled, each one has 2 components + 2 * 2 end components
            } else {
                vertices.size * 2
            })

        var idx = 0

        if(pixelize) {
            if(width == 0 || height == 0 || imageWidth == 0 || imageHeight == 0)
                return FloatArray(0)

            val list = RenderUtil.listNormToPx(vertices, width, height, imageWidth, imageHeight)

            if(drawing) {

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
            vertices.forEach {
                verticesF[idx] = it.x
                verticesF[++idx] = it.y
                ++idx
            }
        }

        return verticesF
    }

    fun copy(subject: Subject) {
        this.name = subject.name
        this.imagePath = subject.imagePath
        this.vertices = subject.vertices
        this.children = subject.children
    }

    /*----------PARCELABLE----------*/
    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeString(imagePath)
            writeString(name)
            writeList(vertices)
            writeTypedList(children)
        }
    }

    //Flag for something, no use for it atm
    override fun describeContents(): Int = 0

    //TODO: handle null-receiving
    constructor(parcel: Parcel) : this(parcel.readString()) {
        name = parcel.readString()
        parcel.readList(vertices, Point::class.java.classLoader)
        children = parcel.createTypedArrayList(CREATOR).toMutableList()
    }

    companion object CREATOR : Parcelable.Creator<Subject> {

        override fun createFromParcel(parcel: Parcel) = Subject(parcel)

        override fun newArray(size: Int): Array<Subject?> = arrayOfNulls(size)
    }
    /*-----------------------------*/
}