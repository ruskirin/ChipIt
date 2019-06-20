package creations.rimov.com.chipit.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.DatabaseApplication
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.database.repos.EditRepo
import creations.rimov.com.chipit.objects.BranchUpFlag
import creations.rimov.com.chipit.objects.CoordPoint
import creations.rimov.com.chipit.objects.FabFlag
import java.text.SimpleDateFormat
import java.util.*

class GlobalViewModel : ViewModel() {

    private val repository = EditRepo(DatabaseApplication.database!!)

    var chipFragParentId: Long = 0L

    private val albumChip: MutableLiveData<ChipIdentity> = MutableLiveData()

    fun getAlbumChip() = albumChip

    fun setAlbumChip(chip: ChipIdentity) {
        albumChip.postValue(chip)
    }

    fun saveChipFragParentId(id: Long) {
        chipFragParentId = id
    }

//    fun saveChip(name: String, imgLocation: String?, vertices: List<CoordPoint>?) {
//
//        val chip = Chip(0, parentId.value ?: -1L, false,
//            name, "",
//            SimpleDateFormat("MM-dd-yyy", Locale.US).format(Date()),
//            imgLocation = imgLocation ?: "", vertices = vertices)
//
//        Log.i("Chip Creation", "WebViewModel#saveChip(): saving chip under parent $parentId")
//
//        repository.insertChip(chip)
//    }
}