package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import creations.rimov.com.chipit.R

class EditorMatPrevAudioLayout(context: Context, attrs: AttributeSet)
    : ConstraintLayout(context, attrs) {

    //TODO NOW: remember to change the image of play button dynamically

    init {
        View.inflate(context, R.layout.viewgroup_audio, this)
    }
}