package creations.rimov.com.chipit.view_models

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.objects.Point
import creations.rimov.com.chipit.objects.Subject
import creations.rimov.com.chipit.util.RenderUtil
import creations.rimov.com.chipit.util.TextureUtil
import java.lang.Exception

class ChipViewModel : ViewModel() {

    //Main Subject
    private val subj: Subject = Subject()
    //Child Subject
    private var childSubj: Subject? = Subject()
    //Bitmap of Main Subject
    private lateinit var subjBitmap: Bitmap

    //Set the main Subject, return true if completed, otherwise false
    fun setSubject(subject: Subject?): Boolean {

        if(subject == null)
            return false

        if(subject.imagePath == "") {
            Log.e("ChipViewModel", "setSubject: imagePath empty")
            return false
        }

        subj.copy(subject)
        setSubjectBitmap(subj.imagePath)

        return true
    }

    fun getSubject() = subj

    //Prepare a new child Subject
    fun initChip(chip: Subject? = null) {

        if(chip != null) {
            if(childSubj == null)
                childSubj = Subject()

            childSubj!!.copy(chip)
        }

        childSubj = Subject()
    }

    fun addChipVertex(point: Point) {

        childSubj?.vertices?.add(point)
    }

    //Save temp Subject {@param childSubj} in subj.children
    fun saveChip() {

        if (childSubj != null && childSubj!!.vertices.size > 3) {
            subj.children.add(childSubj!!)

            Log.i("Vertex Rendering", "saved x = ${childSubj!!.vertices[0].x}, y = ${childSubj!!.vertices[0].y}")

        } else {
            Log.e("ChipViewModel", "initChip: not enough vertices in childSubj")
        }

        childSubj = null
    }

    private fun setSubjectBitmap(path: String) {

        try {
            subjBitmap = TextureUtil.convertPathToBitmap(path)!!

        } catch(e: Exception) {
            //TODO: handle specific exception
            e.printStackTrace()
        }
    }

    fun getSubjectBitmap() = subjBitmap
    fun getSubjectBitmapWidth() = subjBitmap.width
    fun getSubjectBitmapHeight() = subjBitmap.height
}