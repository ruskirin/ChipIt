package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.test.espresso.idling.CountingIdlingResource
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.constants.EditorConsts
import creations.rimov.com.chipit.constants.MediaConsts
import creations.rimov.com.chipit.extensions.activity
import creations.rimov.com.chipit.extensions.displayImage
import creations.rimov.com.chipit.extensions.gone
import creations.rimov.com.chipit.extensions.visible
import creations.rimov.com.chipit.viewgroups.custom.CustomAudioLayout
import creations.rimov.com.chipit.viewgroups.custom.CustomVideoLayout
import creations.rimov.com.chipit.viewgroups.custom.CustomView
import kotlinx.android.synthetic.main.editor_material.view.*
import kotlinx.android.synthetic.main.editor_material_image.view.*
import kotlinx.coroutines.Runnable

class EditorMatPrevLayout(context: Context, attrs: AttributeSet)
    : ConstraintLayout(context, attrs),
      CustomView<EditorMatPrevLayout.Handler>,
      MediaPlayer.OnPreparedListener,
      MediaPlayer.OnCompletionListener,
      MediaPlayer.OnInfoListener,
      MediaPlayer.OnErrorListener,
      CustomVideoLayout.Handler,
      View.OnClickListener {

    override lateinit var handler: Handler

    private val threadHandler: android.os.Handler by lazy {Handler()}
    private lateinit var controlsRunnable: Runnable

    private var player: MediaPlayer? = MediaPlayer()
    private var playerState: Int = MediaConsts.IDLE

    private val btnAddMat: Button by lazy {btnAddMatPrev}
    private val imgLayout: ImageView by lazy {promptPreviewImg}
    private val videoLayout: CustomVideoLayout by lazy {matPreviewVideoLayout}
    private val audioLayout: CustomAudioLayout by lazy {matPreviewAudioLayout}

    init {
        View.inflate(context, R.layout.editor_material, this)

        videoLayout.prepare(this)

        btnAddMat.setOnClickListener(this)
        imgLayout.setOnClickListener(this)
        videoLayout.setOnClickListener(this)

        player?.apply {
            setOnPreparedListener(this@EditorMatPrevLayout)
            setOnCompletionListener(this@EditorMatPrevLayout)
            setOnInfoListener(this@EditorMatPrevLayout)
            setOnErrorListener(this@EditorMatPrevLayout)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        Log.i("EditorMatPrevLayout", "::onDetachedFromWindow(): " +
                                     "releasing player!")

        player?.release()
        player = null

        playerState = MediaConsts.DEAD
    }

    override fun onClick(v: View?) {
        if(::handler.isInitialized) handler.promptAddMat()
    }

    override fun videoSeekTo(time: Int) {

        if(playerState != MediaConsts.PREPARED
           && playerState != MediaConsts.COMPLETED)

            Log.i("EditorMatPrevLayout",
                  "#videoSeekTo(): seeking to $time")

            player?.seekTo(time)
    }

    override fun videoAction(action: Int) {

        when(action) {
            MediaConsts.PLAY -> {
                if(playerState == MediaConsts.PREPARED
                   || playerState == MediaConsts.COMPLETED) {

                    videoLayout.setPlaying(playing = true)
                    player?.start()

                    context?.activity()?.runOnUiThread(
                      object : Runnable {
                          override fun run() {
                              player?.let {
                                  videoLayout.setSeekbarProgress(
                                    it.currentPosition/1000)

                                  threadHandler.postDelayed(this, 500)

                                  Log.i("EditorMatPrevLayout",
                                            "#videoAction(): updating seekbar")
                              }
                          }
                      })
                }
            }
            MediaConsts.PAUSE -> {
                player?.apply {
                    if(isPlaying) pause()
                    videoLayout.setPlaying(playing = false)

                    threadHandler.removeCallbacksAndMessages(null)
                }
            }
            MediaConsts.STOP -> {
                if(playerState != MediaConsts.IDLE) player?.stop()
                videoLayout.setPlaying(playing = false)

                threadHandler.removeCallbacksAndMessages(null)
            }
        }
    }

    override fun onPrepared(player: MediaPlayer?) {

        player?.let {
            Log.i("EditorMatPrevLayout", "::onPrepared()")
            playerState = MediaConsts.PREPARED

            videoLayout.initControls(player.duration/1000L)
        }
    }

    override fun onCompletion(player: MediaPlayer?) {

        player?.let {
            Log.i("EditorMatPrevLayout", "::onCompletion()")
            playerState = MediaConsts.COMPLETED
            videoLayout.setPlaying(false)

            threadHandler.removeCallbacksAndMessages(null)
        }
    }

    override fun onInfo(
      player: MediaPlayer?, warning: Int, extra: Int): Boolean {

        when(warning) {
            MediaPlayer.MEDIA_INFO_UNKNOWN -> {
                Log.i("EditorMatPrevLayout",
                      "::onInfo(): " + "cannot get info on player!")

                return true
            }
            MediaPlayer.MEDIA_INFO_NOT_SEEKABLE -> {
                Log.i("EditorMatPrevLayout",
                  "::onInfo(): " + "cannot seek to video time!")

                return true
            }
        }

        return false
    }

    override fun onError(
      player: MediaPlayer?, warning: Int, extra: Int): Boolean {

        when(warning) {
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                Log.e("EditorMatPrevLayout",
                      "::onError(): unknown error!")

                return true
            }
        }

        when(extra) {
            MediaPlayer.MEDIA_ERROR_IO -> {
                Log.e("EditorMatPrevLayout",
                      "::onError(): IO error!")

                return true
            }
            MediaPlayer.MEDIA_ERROR_MALFORMED -> {
                Log.e("EditorMatPrevLayout",
                      "::onError(): video malformed error!")

                return true
            }
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> {
                Log.e("EditorMatPrevLayout",
                      "::onError(): unsupported format error!")

                return true
            }
            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
                Log.e("EditorMatPrevLayout",
                      "::onError(): timed out error!")

                return true
            }
        }

        return false
    }

    override fun setPlayerDisplay(holder: SurfaceHolder?) {
        player?.apply {setDisplay(holder)}
    }

    private fun <T> playerPrepare(path: T?) {

        player?.reset()

        if(player == null) player = MediaPlayer()

        when(path) {
            is Uri -> {
                player?.setDataSource(context.applicationContext, path)
                player?.prepareAsync()
            }
            is String -> {
                player?.setDataSource(path)
                player?.prepareAsync()
            }
            else -> return
        }
    }

    /**
     * For when a resource already exists and needs to be displayed
     */
    fun <T> display(matType: Int?, matPath: T?) {

        when(matType) {
            EditorConsts.IMAGE -> {
                btnAddMat.gone()
                imgLayout.visible()
                videoLayout.gone()
                audioLayout.gone()

                imgLayout.displayImage(matPath)
            }
            EditorConsts.VIDEO  -> {
                playerPrepare(
                  when(matPath) {
                      is Uri -> matPath
                      is String -> matPath.toUri()
                      else -> return
                  })

                btnAddMat.gone()
                imgLayout.gone()
                videoLayout.visible()
                audioLayout.gone()
            }
            EditorConsts.AUDIO  -> {
                btnAddMat.gone()
                imgLayout.gone()
                videoLayout.gone()
                audioLayout.visible()
            }
            EditorConsts.TEXT  -> {
                //TODO HIGH: implement text material
            }
            else -> {
                btnAddMat.visible()
                imgLayout.gone()
                videoLayout.gone()
                audioLayout.gone()
            }
        }
    }

    override fun prepare(handler: Handler, vararg opts: Any?) {
        this.handler = handler
    }


    interface Handler {

        fun promptAddMat()
    }
}