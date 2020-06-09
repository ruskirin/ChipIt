package creations.rimov.com.chipit.viewgroups.custom

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.test.espresso.idling.CountingIdlingResource
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.constants.MediaConsts
import creations.rimov.com.chipit.extensions.secToTimeFormat
import kotlinx.android.synthetic.main.viewgroup_video.view.*

class CustomVideoLayout(context: Context, attrs: AttributeSet)
    : ConstraintLayout(context, attrs),
      SurfaceHolder.Callback,
      SeekBar.OnSeekBarChangeListener,
      CustomView<CustomVideoLayout.Handler> {

    override lateinit var handler: Handler

    private val surface: SurfaceView by lazy {videoSurface}
    private val controls: CustomPlaybackControls by lazy {playbackControls}

    init {
        View.inflate(context, R.layout.viewgroup_video, this)

        surface.holder.addCallback(this)

        controls.seekBar.setOnSeekBarChangeListener(this)
    }

    fun setSeekbarProgress(time: Int) {

        controls.seekBar.progress = time
        controls.timeCurrent.text = time.toLong().secToTimeFormat()
    }

    fun setPlaying(playing: Boolean) {
        controls.setPlaying(playing)
    }

    fun initControls(timeTotal: Long) {

        controls.seekBar.max = timeTotal.toInt()
        controls.timeTotal.text = timeTotal.secToTimeFormat()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        holder?.let {handler.setPlayerDisplay(it)}
    }

    override fun surfaceChanged(holder: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        holder?.let {handler.setPlayerDisplay(null)}
    }

    override fun onProgressChanged(
      seekbar: SeekBar?, progress: Int, fromUser: Boolean) {

        seekbar?.let {
            if(fromUser) handler.videoSeekTo(progress*1000)
        }
    }

    override fun onStartTrackingTouch(seekbar: SeekBar?) {
        handler.videoAction(MediaConsts.PAUSE)
    }

    override fun onStopTrackingTouch(seekbar: SeekBar?) {
        handler.videoAction(MediaConsts.PLAY)
    }

    override fun prepare(handler: Handler, vararg opts: Any?) {
        this.handler = handler

        controls.prepare(handler)
    }


    interface Handler {

        fun setPlayerDisplay(holder: SurfaceHolder?)
        fun videoAction(action: Int)
        fun videoSeekTo(time: Int)
    }
}