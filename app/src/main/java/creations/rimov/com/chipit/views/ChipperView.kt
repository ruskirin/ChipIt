package creations.rimov.com.chipit.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class ChipperView(cont: Context, attribs: AttributeSet) : SurfaceView(cont, attribs), SurfaceHolder.Callback {

    init {
        holder.addCallback(this)
    }

    //Interface instance implemented in ChipActivity
    private lateinit var chipHandler: ChipHandler

    override fun surfaceCreated(holder: SurfaceHolder?) {

    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        chipHandler.setScreenDimen()
        chipHandler.setBitmapRect()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        //TODO (FUTURE): wrap up work
    }

    /**
     * Dynamic draw calls go here
     */
    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

        if(canvas != null)
            chipHandler.drawScreen(canvas)
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

        fun drawScreen(canvas: Canvas)

        fun setScreenDimen()

        fun setBitmapRect()
    }
}