package creations.rimov.com.chipit.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import creations.rimov.com.chipit.data.DatabaseApplication
import creations.rimov.com.chipit.data.objects.ChipIdentity
import creations.rimov.com.chipit.data.objects.ChipPath
import creations.rimov.com.chipit.data.repos.AccessRepo
import creations.rimov.com.chipit.data.repos.RepoHandler
import creations.rimov.com.chipit.util.objects.CoordPoint
import creations.rimov.com.chipit.util.TextureUtil
import kotlin.math.roundToInt

class ChipperViewModel(
  application: Application,
  private val handler: RepoHandler) : AndroidViewModel(application) {

    private val repository = AccessRepo(DatabaseApplication.database!!, handler)

    private val chipId: MutableLiveData<Long?> = MutableLiveData()
    //The chip currently viewed in the background and being worked on
    private val chip: LiveData<ChipIdentity> = Transformations.switchMap(chipId) {
        repository.getChipIdentityLive(it)
    }
    //Children of the main chip
    private val children: LiveData<List<ChipPath>> = Transformations.switchMap(chipId) {
        repository.getChipPathsLive(it)
    }

    var backgroundBitmap: Bitmap? = null


    fun setChipId(id: Long?) {
        this.chipId.postValue(id)
    }

    fun getChip(): LiveData<ChipIdentity> = chip

    fun getChildren(): LiveData<List<ChipPath>>? = children

    /**Check if point landed inside one of the children**/
    fun getTouchedChip(point: CoordPoint): ChipPath? {

        children.value?.forEach { chip ->
            if(chip.isInside(point)) return chip
        }

        return null
    }

    fun getBitmapDimen(): Array<Int>? {

        chip.value?.let {
            return TextureUtil.getBitmapDimen(getApplication(), it.matPath)
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

        chip.value?.let {
            val stream = getApplication<Application>().contentResolver.openInputStream(Uri.parse(it.matPath))

            TextureUtil.AsyncPathToBitmap(handler, sampleSize).execute(stream)
        }
    }
}