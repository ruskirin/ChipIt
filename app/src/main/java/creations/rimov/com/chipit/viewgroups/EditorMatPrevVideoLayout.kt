package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import creations.rimov.com.chipit.R
import kotlinx.android.synthetic.main.editor_material_video.view.*
import kotlinx.android.synthetic.main.viewgroup_play_controls.view.*

class EditorMatPrevVideoLayout(context: Context, attrs: AttributeSet)
    : ConstraintLayout(context, attrs) {

    private val video: VideoView by lazy {promptPrevVideo}
    private val controlsLayout: View by lazy {promptPrevVideoControls}
    private val btnLeft: ImageButton by lazy {btnPlaybackLeft}
    private val btnPlay: ImageButton by lazy {btnPlaybackPlay}
    private val btnRight: ImageButton by lazy {btnPlaybackRight}

    init {
        View.inflate(context, R.layout.editor_material_video, this)
    }
}