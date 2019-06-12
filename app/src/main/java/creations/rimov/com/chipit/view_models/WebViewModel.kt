package creations.rimov.com.chipit.view_models

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.objects.ChipPath
import creations.rimov.com.chipit.database.repos.WebRepository
import creations.rimov.com.chipit.objects.CoordPoint
import creations.rimov.com.chipit.util.TextureUtil
import java.lang.Exception

class WebViewModel : ViewModel(), WebRepository.ChipRepoHandler {

    private val repository = WebRepository(DatabaseApplication.database!!, this)

    private val parentId: MutableLiveData<Long> = MutableLiveData()
    //The chip currently viewed in the background and being worked on
    private val parent: LiveData<ChipIdentity> = Transformations.switchMap(parentId) {
        repository.getChipIdentity(it)
    }
    //Children of the main chip
    private val children: LiveData<List<ChipPath>> = Transformations.switchMap(parentId) {
        repository.getChildrenPaths(it)
    }

    private lateinit var bitmap: Bitmap
    //Saved image path for present bitmap
    private var imgLocation: String = ""

    //Flag to redraw background bitmap
    var backgroundChanged = false
    //Is the parent of the current parent a topic? Used to toggle branch up navigation
    var canNavigateUp = false


    fun setParentId(parentId: Long) {

        if(parentId != this.parentId.value) {
            this.parentId.postValue(parentId)
        }
    }

    fun getParentId() = parentId.value

    fun getParentIdOfParent() = parent.value?.parentId ?: -1L

    fun getParent(): LiveData<ChipIdentity>? = parent

    fun getChildren(): LiveData<List<ChipPath>>? = children

    fun checkUpNavigation() {
        repository.isChipTopic(parent.value?.parentId ?: -1L)
    }

    /**Check if point landed inside one of the children**/
    fun getTouchedChip(point: CoordPoint): ChipPath? {

        children.value?.forEach { chip ->
            if(chip.isInside(point)) {
                return chip
            }
        }

        return null
    }

    fun setBitmap(imgLocation: String) {
        this.imgLocation = imgLocation

        try {
            bitmap = TextureUtil.convertPathToBitmap(imgLocation)!!

        } catch(e: Exception) {
            Log.e("WebViewModel", "#setBitmap(): could not create bitmap from passed image path!")
            e.printStackTrace()

        } finally {
            backgroundChanged = true
        }
    }

    fun getBitmap() =
        if(::bitmap.isInitialized) bitmap
        else null

    fun getBitmapWidth() =
        if(::bitmap.isInitialized) bitmap.width
        else 0

    fun getBitmapHeight() =
        if(::bitmap.isInitialized) bitmap.height
        else 0

    fun updateChip(id: Long,
                   name: String = "",
                   imgLocation: String? = null,
                   vertices: List<CoordPoint>? = null) {

        repository.updateChip(id, name, imgLocation, vertices)
    }

    fun saveChip(name: String, imgLocation: String?, vertices: List<CoordPoint>?) {
        val chip = Chip(0, parentId.value ?: -1L, false, name, imgLocation ?: "", vertices)

        Log.i("Chip Creation", "WebViewModel#saveChip(): saving chip under parent $parentId")

        repository.insertChip(chip)
    }

    override fun isParentTopic(isTopic: Boolean) {


        this.canNavigateUp = !isTopic
    }
}