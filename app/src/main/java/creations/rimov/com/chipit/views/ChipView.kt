package creations.rimov.com.chipit.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class ChipView(cont: Context, attribs: AttributeSet)
    : SurfaceView(cont, attribs), SurfaceHolder.Callback {

    init {
        holder.addCallback(this)
    }

    //Interface instance implemented in ChipActivity
    private lateinit var chipListener: ChipListener

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.i("ChipView Life", "surfaceCreated!")

        chipListener.setScreenDimen()
        chipListener.setBitmapRect()
        chipListener.initSurface()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        Log.i("ChipView Life", "surfaceChanged!")

        chipListener.setScreenDimen()
        chipListener.setBitmapRect()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        Log.i("ChipView Life", "surfaceDestroyed!")

        /**TODO: Save data**/
    }

    /**
     * Dynamic draw calls go here
     */
    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

        if(canvas != null)
            chipListener.drawScreen(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {

            MotionEvent.ACTION_DOWN -> {
                chipListener.chipStart(event.x, event.y)
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                chipListener.chipDrag(event.x, event.y)
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                chipListener.chipEnd(event.x, event.y)
                invalidate()
            }
        }

        return true
    }

    fun setListener(listener: ChipListener) {
        chipListener = listener
    }

    interface ChipListener {

        fun chipStart(x: Float, y: Float)

        fun chipDrag(x: Float, y: Float)

        fun chipEnd(x: Float, y: Float)

        fun drawScreen(canvas: Canvas)

        fun setScreenDimen()

        fun setBitmapRect()

        fun initSurface()
    }
}