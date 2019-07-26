package creations.rimov.com.chipit.view_models

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.objects.ChipPath
import creations.rimov.com.chipit.database.repos.AccessRepo
import creations.rimov.com.chipit.objects.CoordPoint
import creations.rimov.com.chipit.util.TextureUtil
import kotlin.math.roundToInt

class ChipperViewModel(application: Application) : AndroidViewModel(application), AccessRepo.RepoHandler {

    private val repository = AccessRepo(DatabaseApplication.database!!, this)

    private val parentId: MutableLiveData<Long?> = MutableLiveData()
    //The chip currently viewed in the background and being worked on
    private val parent: LiveData<ChipIdentity> = Transformations.switchMap(parentId) {
        repository.getChipIdentityLive(it)
    }
    //Children of the main chip
    private val children: LiveData<List<ChipPath>> = Transformations.switchMap(parentId) {
        repository.getChipPathsLive(it)
    }

    var bitmap: Bitmap? = null


    fun setParentId(parentId: Long) {

        if(parentId != this.parentId.value) {
            this.parentId.postValue(parentId)
        }
    }

    fun getParentId() = parentId.value

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

    fun getBitmapDimen(): Array<Int>? {

        //TODO URGENT: problem with loading background in chipper comes from here, as this is called before the livedata is
        //              updated, returning a null and giving no rectangle for the bitmap to populate. Possible solution is to
        //              have the rectangle be set in response to setting the parent

        parent.value?.let {
            return TextureUtil.getBitmapDimen(getApplication(), it.imgLocation)
        }

        return null
    }

    /** @params bitmapWidth, bitmapHeight: actual size of bitmap
     *  @params reqWidth, reqHeight: size of the display window
     *
     * First find an appropriate sampleSize to scale the bitmap down to, then get the bitmap
     */
    fun setBitmap(bitmapWidth: Int, bitmapHeight: Int, reqWidth: Int, reqHeight: Int) {

        var sampleSize: Int = 1

        if(bitmapWidth > reqWidth || bitmapHeight > reqHeight) {
            val wRatio = bitmapWidth.toFloat() / reqWidth
            val hRatio = bitmapHeight.toFloat() / reqHeight

            sampleSize = if(wRatio < hRatio) wRatio.roundToInt() else hRatio.roundToInt()
        }

        parent.value?.let {
            val stream = getApplication<Application>().contentResolver.openInputStream(Uri.parse(it.imgLocation))

            TextureUtil.AsyncPathToBitmap(this, sampleSize).execute(stream)
        }
    }

    override fun <T> setData(data: T) {

        when(data) {
            is Bitmap? -> {
                bitmap = data
                //Cache the view in preparation of canvas draw
                bitmap?.prepareToDraw()
            }
        }
    }

    override fun <T> setDataList(data: List<T>) {}
}