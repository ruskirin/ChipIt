package creations.rimov.com.chipit.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.objects.BranchUpFlag
import creations.rimov.com.chipit.objects.FabFlag

class GlobalViewModel : ViewModel() {

    var chipFragParentId: Long = 0L

    private val albumChip: MutableLiveData<ChipIdentity> = MutableLiveData()

    private val fabFlag = MutableLiveData<FabFlag>()
    private val upFlag = MutableLiveData<BranchUpFlag>()

    fun getAlbumChip() = albumChip

    fun setAlbumChip(chip: ChipIdentity) {
        albumChip.postValue(chip)
    }

    fun getFabFlag() = fabFlag

    fun displayFab(display: Boolean) {
        fabFlag.postValue(FabFlag(display))
    }

    fun touchFab(touch: Boolean) {
        fabFlag.postValue(FabFlag(touched = touch))
    }

    fun getUpFlag() = upFlag

    fun displayUp(display: Boolean) {
        upFlag.postValue(BranchUpFlag(display))
    }

    fun touchUp(touch: Boolean) {
        upFlag.postValue(BranchUpFlag(touched = touch))
    }

    fun saveChipFragParentId(id: Long) {
        chipFragParentId = id
    }
}