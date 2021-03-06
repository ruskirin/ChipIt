package creations.rimov.com.chipit.ui.chipper

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class ChipperView(cont: Context, atts: AttributeSet)
    : SurfaceView(cont, atts), SurfaceHolder.Callback {

    init {
        holder.addCallback(this)
    }

    //Interface instance implemented in ChipActivity
    private lateinit var chipHandler: ChipHandler

    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceChanged(
      holder: SurfaceHolder,
      format: Int,
      width: Int,
      height: Int) {

        chipHandler.setScreenDimen(width, height)
        chipHandler.setBitmapRect()

        chipHandler.drawBackground(holder)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        //TODO (FUTURE): wrap up work
    }

    /**
     * Dynamic draw calls go here
     */
    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

        canvas?.let {chipHandler.drawScreen(it)}
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when(event?.action) {

            MotionEvent.ACTION_DOWN -> {
                chipHandler.setAction(event.x, event.y)

                chipHandler.touchDown(event.x, event.y)
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                chipHandler.touchDrag(event.x, event.y)
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                chipHandler.touchUp(event.x, event.y)
                invalidate()
            }
        }

        return true
    }

    fun setHandler(handler: ChipHandler) {
        chipHandler = handler
    }

    interface ChipHandler {

        fun setAction(x: Float, y: Float)

        fun touchDown(x: Float, y: Float)

        fun touchDrag(x: Float, y: Float)

        fun touchUp(x: Float, y: Float)

        fun drawBackground(holder: SurfaceHolder?)

        fun drawScreen(canvas: Canvas)

        fun setScreenDimen(width: Int, height: Int)

        fun setBitmapRect()
    }
}