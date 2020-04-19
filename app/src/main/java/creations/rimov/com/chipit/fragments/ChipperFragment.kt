package creations.rimov.com.chipit.fragments

import android.app.Application
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.activities.MainActivity
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.repos.AccessRepo
import creations.rimov.com.chipit.objects.ChipAction
import creations.rimov.com.chipit.objects.CoordPoint
import creations.rimov.com.chipit.util.RenderUtil
import creations.rimov.com.chipit.view_models.ChipperTouchViewModel
import creations.rimov.com.chipit.view_models.ChipperViewModel
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.views.ChipperView
import kotlinx.android.synthetic.main.chipper.view.*

class ChipperFragment : Fragment(), ChipperView.ChipHandler, View.OnTouchListener, AccessRepo.RepoHandler {

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
    private lateinit var localViewModel: ChipperViewModel

    private val localTouchViewModel: ChipperTouchViewModel by lazy {
        ViewModelProvider(this).get(ChipperTouchViewModel::class.java)
    }

    private lateinit var chipperView: ChipperView

    //Aspect ratio Rect frame for subject image
    private val backgroundRect: Rect = Rect()

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    //Current touch action
    private var surfaceAction: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            globalViewModel = ViewModelProvider(it).get(GlobalViewModel::class.java)

            localViewModel = ViewModelProvider(
              this, ChipperVmFactory(it.application, this))
                .get(ChipperViewModel::class.java)
        }

        globalViewModel.getPrimaryChip().observe(this, Observer {
            localViewModel.setChipId(it?.id)
        })
    }

    override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.chipper, container, false)

        chipperView = view.chipperSurfaceView.apply {
            setHandler(this@ChipperFragment)
            setOnTouchListener(this@ChipperFragment)
        }

        localViewModel.getChip().observe(viewLifecycleOwner, Observer {
            chipperView.invalidate()

            setBitmapRect()
        })

        localViewModel.getChildren()?.observe(viewLifecycleOwner, Observer {
            chipperView.invalidate()
        })

        localTouchViewModel.pathCreated.observe(viewLifecycleOwner, Observer { created ->

            if(!created) return@Observer

            globalViewModel.setChipAction(
                  ChipAction.instance(
                        Chip(0L,
                             globalViewModel.getPrimaryId(),
                             vertices = localTouchViewModel.getPathVertices()),
                        MainActivity.EditorAction.CREATE))
        })

        return view
    }

    private fun drawChildren(canvas: Canvas) {

        if(viewWidth == 0
           || viewHeight == 0
           || backgroundRect.width() == 0
           || backgroundRect.height() == 0) return

        localViewModel.getChildren()?.value?.forEach { chip ->

            if(chip.vertices.isNullOrEmpty() || chip.vertices.size <= 3)
                return@forEach

            Log.i("Chip Creation", "ChipperFrag#drawChildren(): " +
                                   "drawing chip ${chip.id}")

            canvas.drawLines(
                chip.getVerticesFloatArray(
                    true,
                    viewWidth, viewHeight,
                    backgroundRect.width(), backgroundRect.height())!!,
                localTouchViewModel.getDrawPaint())
        }
    }

    override fun drawBackground(holder: SurfaceHolder?) {

        val chipHolder = holder ?: chipperView.holder
        val canvas = chipHolder.lockCanvas() ?: return

        //Draw static images (eg. background, chip pathways)
        try {
            synchronized(chipHolder) {
                //TODO (FUTURE): load in a default bitmap if this one cannot be
                // loaded
                canvas.drawBitmap(
                  localViewModel.backgroundBitmap ?: return,
                  null, backgroundRect,
                  null)
            }

            //TODO: handle error
        } catch (e: Throwable) {
            e.printStackTrace()

        } finally {
            chipHolder.unlockCanvasAndPost(canvas)
        }
    }

    override fun drawScreen(canvas: Canvas) {

        canvas.drawPath(
          localTouchViewModel.getDrawPath(), localTouchViewModel.getDrawPaint())

        drawChildren(canvas)
    }

    override fun setBitmapRect() {

        localTouchViewModel.initPaint()

        //Pos 0 = width, pos 1 = height
        val dimen = localViewModel.getBitmapDimen() ?: return

        backgroundRect.set(
              RenderUtil.getAspectRatioRect(
                dimen[0], dimen[1], viewWidth, viewHeight))

        localViewModel.setBitmap(dimen[0], dimen[1], viewWidth, viewHeight)
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
            point0.normalize(
              viewWidth, viewHeight, backgroundRect.width(), backgroundRect.height()))

        if(touchedChip != null) {
            surfaceAction = Constant.NAV_CHIP

            Log.i("Touch Event", "ChipperFragment#instance(): " +
                                 "touched chip: ${touchedChip.id}")

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
                    CoordPoint(x, y), viewWidth, viewHeight, backgroundRect.width(), backgroundRect.height())
            }

            Constant.NAV_CHIP -> {
                val touchedChip = localViewModel.getTouchedChip(
                    CoordPoint(x, y).normalize(viewWidth, viewHeight, backgroundRect.width(), backgroundRect.height()))

                if(touchedChip != null) {
                    globalViewModel.setPrimaryChip(
                          touchedChip.asChip(globalViewModel.getPrimaryChip().value?.id))
                }
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

    // AccessRepo.RepoHandler-----------------------------------------------------------------
    override fun <T> setData(data: T) {

        when(data) {
            is Bitmap? -> {
                localViewModel.backgroundBitmap = data

                drawBackground(null)
            }
        }
    }

    override fun <T> setDataList(data: List<T>) {}
    //----------------------------------------------------------------------------------------

    override fun setScreenDimen(width: Int, height: Int) {

        viewWidth = width
        viewHeight = height
    }

    /**
     * ViewModel factory for passing the RepoHandler
     */
    class ChipperVmFactory(private val application: Application,
                           private val handler: AccessRepo.RepoHandler) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChipperViewModel(application, handler) as T
        }
    }
}