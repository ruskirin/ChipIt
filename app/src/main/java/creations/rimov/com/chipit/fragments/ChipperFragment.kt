package creations.rimov.com.chipit.fragments

import android.graphics.*
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.objects.CoordPoint
import creations.rimov.com.chipit.util.RenderUtil
import creations.rimov.com.chipit.view_models.ChipperTouchViewModel
import creations.rimov.com.chipit.view_models.ChipperViewModel
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.views.ChipperView
import kotlinx.android.synthetic.main.chipper_layout.*
import kotlinx.android.synthetic.main.chipper_layout.view.*

class ChipperFragment : Fragment(), ChipperView.ChipHandler, View.OnTouchListener {

    private object Constant {
        //Buffer around surfaceview edge to trigger gesture swipe events
        const val EDGE_TOUCH_BUFFER = 30f
        //Swipe distance to trigger event
        const val SWIPE_BUFFER = 150f

        const val DRAW_PATH = 300
        const val SWIPE_EDGE = 400
        const val NAV_CHIP = 500
    }

    private lateinit var globalViewModel: GlobalViewModel

    //Passed Bundle from DirectoryFragment
    private val passedArgs by navArgs<ChipperFragmentArgs>()

    private lateinit var chipperView: ChipperView
    private lateinit var chipHolder: SurfaceHolder

    private val localViewModel: ChipperViewModel by lazy {
        ViewModelProviders.of(this).get(ChipperViewModel::class.java)
    }
    private val localTouchViewModel: ChipperTouchViewModel by lazy {
        ViewModelProviders.of(this).get(ChipperTouchViewModel::class.java)
    }

    //Aspect ratio Rect frame for subject image
    private val parentImageFrame: Rect = Rect()

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    //Current touch action
    private var surfaceAction: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            globalViewModel = ViewModelProviders.of(it).get(GlobalViewModel::class.java)
        }

        if(localViewModel.getParentId() != passedArgs.chipId)
            localViewModel.setParentId(passedArgs.chipId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.chipper_layout, container, false)

        chipperView = view.chipperSurfaceView.apply {
            setHandler(this@ChipperFragment)
            setOnTouchListener(this@ChipperFragment)
        }

        chipHolder = chipperView.holder

        localViewModel.getParent()?.observe(this, Observer { parent ->
            Log.i("Life Event", "ChipperFragment#parentObserver: triggered!")

            Glide.with(this).load(parent.imgLocation).into(view.chipperImage)

            localViewModel.setBitmap(parent.imgLocation)
            setBitmapRect()
        })

        localViewModel.getChildren()?.observe(this, Observer { children ->
            chipperView.invalidate()
        })

        localTouchViewModel.pathCreated.observe(this, Observer { created ->

        })

        return view
    }

    //TODO (FUTURE): have this run on a separate thread, display loading bar
//    private fun drawBackground() {
//
//        if(localViewModel.bitmap == null) return
//
//        localTouchViewModel.initPaint()
//
//        var canvas: Canvas? = null
//
//        DrawBackground(chipHolder, parentImageFrame, canvas).execute(localViewModel.bitmap)
//
//        if(canvas != null) localViewModel.backgroundChanged = false
//    }

    override fun drawScreen(canvas: Canvas) {

//        if(localViewModel.backgroundChanged)
//            drawBackground()

        canvas.drawPath(localTouchViewModel.getDrawPath(), localTouchViewModel.getDrawPaint())

        drawChildren(canvas)
    }

    private fun drawChildren(canvas: Canvas) {

        if(viewWidth == 0 || viewHeight == 0 || parentImageFrame.width() == 0 || parentImageFrame.height() == 0)
            return

        //TODO (FUTURE): should be off UI thread
        localViewModel.getChildren()?.value?.forEach { chip ->

            if(chip.vertices.isNullOrEmpty() || chip.vertices.size <= 3)
                return@forEach

            canvas.drawLines(
                chip.getVerticesFloatArray(
                    true, viewWidth, viewHeight, parentImageFrame.width(), parentImageFrame.height())!!,
                localTouchViewModel.getDrawPaint())
        }
    }

    override fun setScreenDimen() {

        viewWidth = chipperView.measuredWidth
        viewHeight = chipperView.measuredHeight

        localViewModel.backgroundChanged = true
    }

    override fun setBitmapRect() {

        val width = localViewModel.bitmap?.width ?: return
        val height = localViewModel.bitmap?.height ?: return

        parentImageFrame.set(
            RenderUtil.getAspectRatioRect(width, height, viewWidth, viewHeight))
    }

    //Initial point of contact
    private lateinit var point0: CoordPoint

    /**Identify surface touched intention (draw, swipe, etc)**/
    override fun setAction(x: Float, y: Float) {

        point0 = CoordPoint(x, y)

        if((viewHeight - y) < Constant.EDGE_TOUCH_BUFFER) {
            surfaceAction = Constant.SWIPE_EDGE

            return
        }

        val touchedChip = localViewModel.getTouchedChip(
            point0.normalize(viewWidth, viewHeight, parentImageFrame.width(), parentImageFrame.height()))

        if(touchedChip != null) {
            surfaceAction = Constant.NAV_CHIP

            Log.i("Touch Event", "ChipperFragment#instance(): touched chip: ${touchedChip.id}")

            return
        }

        surfaceAction = Constant.DRAW_PATH
    }

    //TODO: (FUTURE) should not be able to draw outside background rectangle
    override fun touchDown(x: Float, y: Float) {

        when(surfaceAction) {

            Constant.DRAW_PATH -> {
                localTouchViewModel.startPath(CoordPoint(x, y))
            }
        }
    }

    override fun touchDrag(x: Float, y: Float) {

        when(surfaceAction) {

            Constant.DRAW_PATH -> {
                localTouchViewModel.dragPath(CoordPoint(x, y))
            }

            Constant.SWIPE_EDGE -> {

            }
        }
    }

    override fun touchUp(x: Float, y: Float) {

        when(surfaceAction) {

            Constant.DRAW_PATH -> {
                localTouchViewModel.endPath(
                    CoordPoint(x, y), viewWidth, viewHeight, parentImageFrame.width(), parentImageFrame.height())
            }

            Constant.NAV_CHIP -> {
                val touchedChip = localViewModel.getTouchedChip(
                    CoordPoint(x, y).normalize(viewWidth, viewHeight, parentImageFrame.width(), parentImageFrame.height()))

                if(touchedChip != null)
                    localViewModel.setParentId(touchedChip.id)
            }
        }
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        if(event == null || event.action == MotionEvent.ACTION_DOWN)
            return false

        when(view?.id) {

        }

        return false
    }


//    class DrawBackground(private val chipHolder: SurfaceHolder,
//                         private val rect: Rect,
//                         private var canvas: Canvas?) : AsyncTask<Bitmap, Void, Void>() {
//
//        override fun doInBackground(vararg params: Bitmap): Void? {
//            //Draw static images (eg. background, chip pathways)
//            try {
//                canvas = chipHolder.lockCanvas(null)
//
//                synchronized(chipHolder) {
//                    //TODO (FUTURE): load in a default bitmap if this one cannot be loaded
//                    canvas?.drawBitmap(params[0], null, rect, null)
//                }
//                //TODO: handle error
//            } catch (e: Throwable) {
//                e.printStackTrace()
//
//            } finally {
//                chipHolder.unlockCanvasAndPost(canvas ?: return null)
//            }
//
//            return null
//        }
//    }
}