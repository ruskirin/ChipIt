package creations.rimov.com.chipit.view_models

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.objects.ChipPath
import creations.rimov.com.chipit.database.repos.AccessRepo
import creations.rimov.com.chipit.objects.CoordPoint
import creations.rimov.com.chipit.util.TextureUtil
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ChipperViewModel : ViewModel(), AccessRepo.RepoHandler {

    private val repository = AccessRepo(DatabaseApplication.database!!, this)

    private val parentId: MutableLiveData<Long> = MutableLiveData()
    //The chip currently viewed in the background and being worked on
    private val parent: LiveData<ChipIdentity> = Transformations.switchMap(parentId) {
        repository.getChipIdentityLive(it)
    }
    //Children of the main chip
    private val children: LiveData<List<ChipPath>> = Transformations.switchMap(parentId) {
        repository.getChipPathsLive(it)
    }

    var bitmap: Bitmap? = null

    //Flag to redraw background bitmap
    var backgroundChanged = false


    fun setParentId(parentId: Long) {

        if(parentId != this.parentId.value) {
            this.parentId.postValue(parentId)
        }
    }

    fun getParentId() = parentId.value

    fun getParentIdOfParent() = parent.value?.parentId ?: -1L

    fun getParent(): LiveData<ChipIdentity>? = parent

    fun getChildren(): LiveData<List<ChipPath>>? = children

    /**Check if point landed inside one of the children**/
    fun getTouchedChip(point: CoordPoint): ChipPath? {

        children.value?.forEach { chip ->
            if(chip.isInside(point)) {
                return chip
            }
        }

        return null
    }

    fun getMutableBitmap() = bitmap?.copy(Bitmap.Config.ARGB_8888, true)

    fun setBitmap(imgLocation: String) {

        try {
            bitmap = TextureUtil.convertPathToBitmap(imgLocation)

        } catch(e: Exception) {
            Log.e("ChipperViewModel", "#setBitmap(): could not dateCreate bitmap from passed image path!")
            e.printStackTrace()

        } finally {
            backgroundChanged = true
        }
    }

    override fun <T> setData(data: T?) {

    }

    override fun <T> setDataList(data: List<T>) {

    }
}