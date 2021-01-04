package creations.rimov.com.chipit.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.util.constants.MediaConsts
import kotlinx.android.synthetic.main.viewgroup_play_controls.view.*

class PlaybackControls(context: Context, attrs: AttributeSet)
    : ConstraintLayout(context, attrs), CustomView<VideoLayout.Handler>,
      View.OnClickListener {

    override lateinit var handler: VideoLayout.Handler

    private var playing: Boolean = false

    private val btnPlay: ImageButton by lazy {btnPlaybackPlay}
    private val btnExtra: ImageButton by lazy {btnPlaybackExtra}
    val timeCurrent: TextView by lazy {playbackTimeCurrent}
    val timeTotal: TextView by lazy {playbackTimeTotal}
    val seekBar: SeekBar by lazy {playbackSeekbar}

    init {
        View.inflate(context, R.layout.viewgroup_play_controls, this)

        btnPlay.setOnClickListener(this)
    }

    override fun prepare(handler: VideoLayout.Handler, vararg opts: Any?) {
        this.handler = handler
    }

    fun setPlaying(playing: Boolean) {
        this.playing = playing

        if(!playing) btnPlay.setImageResource(R.drawable.ic_play)
        else btnPlay.setImageResource(R.drawable.ic_pause)
    }

    override fun onClick(v: View?) {

        when(v?.id) {
            R.id.btnPlaybackPlay -> {
                if(playing) handler.videoAction(MediaConsts.PAUSE)
                else handler.videoAction(MediaConsts.PLAY)
            }
        }
    }
}