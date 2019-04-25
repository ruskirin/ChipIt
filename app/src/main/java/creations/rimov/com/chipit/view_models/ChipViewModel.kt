package creations.rimov.com.chipit.view_models

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.repos.ChipRepository
import creations.rimov.com.chipit.objects.Point
import creations.rimov.com.chipit.util.TextureUtil
import java.lang.Exception

class ChipViewModel : ViewModel() {

    private val chipRepo = ChipRepository(DatabaseApplication.database!!)

    //Parent Chip of focus
    private lateinit var chip: LiveData<Chip>

    private lateinit var chipChildren: LiveData<List<Chip>>
    //Child Chip functioning as a vertex holder for drawn paths
    private var child: Chip? = null
    //Bitmap of Main Subject
    private lateinit var subjBitmap: Bitmap


    fun initChip(chipId: Long): Boolean {
        chip = chipRepo.getChip(chipId)

        if(chip.value != null) {
            chipChildren = chipRepo.getChildren(chipId)
            setSubjectBitmap(chip.value!!.imagePath)

            return true
        }

        return false
    }

    //Set the main Subject, return true if completed, otherwise false
    /*fun setSubject(subject: Subject?): Boolean {

        if(subject == null)
            return false

        if(subject.imagePath == "") {
            Log.e("ChipViewModel", "setSubject: imagePath empty")
            return false
        }

        subj.copy(subject)
        setSubjectBitmap(subj.imagePath)

        return true
    }*/

    fun getChip() =
        if(::chip.isInitialized)
            chip
        else
            null

    fun getChipChildren() =
        if(::chipChildren.isInitialized)
            chipChildren
        else
            null

    fun initChild(child: Chip? = null) {

        if(::chip.isInitialized) {
            if(this.child == null) {
                this.child = child
                this.child!!.parentId = this.chip.value!!.id
            }
        }

        this.child = Chip(0, this.chip.value!!.id, "", "", mutableListOf())
    }

    //Prepare a new child Subject
    /*fun initChild(chip: Subject? = null) {

        if(chip != null) {
            if(childSubj == null)
                childSubj = Subject()

            childSubj!!.copy(chip)
        }

        childSubj = Subject()
    }*/

    fun addChipVertex(point: Point) {

        //TODO: have a better null check
        if(child != null && child!!.vertices != null)
            child!!.vertices!!.add(point)
    }

    fun saveChild() {

        //TODO: have a better null check
        if (child != null && child!!.vertices!!.size > 3) {
            chipRepo.insertChip(child!!)

        } else {
            Log.e("ChipViewModel", "initChild: not enough vertices in childSubj")
        }

        child = null
    }

    //Save temp Subject {@param childSubj} in subj.children
    /*fun saveChild() {

        if (childSubj != null && childSubj!!.vertices.size > 3) {
            subj.children.add(childSubj!!)

            Log.i("Vertex Rendering", "saved x = ${childSubj!!.vertices[0].x}, y = ${childSubj!!.vertices[0].y}")

        } else {
            Log.e("ChipViewModel", "initChild: not enough vertices in childSubj")
        }

        childSubj = null
    }*/

    private fun setSubjectBitmap(path: String) {

        try {
            subjBitmap = TextureUtil.convertPathToBitmap(path)!!

        } catch(e: Exception) {
            //TODO: handle specific exception
            e.printStackTrace()
        }
    }

    fun getSubjectBitmap() = subjBitmap
    fun getSubjectBitmapWidth() =
        if(::subjBitmap.isInitialized)
            subjBitmap.width
        else
            0

    fun getSubjectBitmapHeight() =
        if(::subjBitmap.isInitialized)
            subjBitmap.height
        else
            0
}