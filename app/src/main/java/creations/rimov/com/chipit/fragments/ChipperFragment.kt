package creations.rimov.com.chipit.fragments

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.adapters.ChipperRecyclerAdapter
import creations.rimov.com.chipit.objects.CoordPoint
import creations.rimov.com.chipit.util.RenderUtil
import creations.rimov.com.chipit.view_models.ChipperTouchViewModel
import creations.rimov.com.chipit.view_models.ChipperViewModel
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.views.ChipperView
import kotlinx.android.synthetic.main.chipper_layout.view.*

class ChipperFragment : Fragment(), ChipperView.ChipHandler, ChipperRecyclerAdapter.WebAdapterHandler, View.OnTouchListener {

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
    private val passedArgs by navArgs<WebFragmentArgs>()

    private lateinit var chipperView: ChipperView
    private lateinit var chipHolder: SurfaceHolder

    private lateinit var recyclerVisBtn: ImageButton
    private lateinit var recyclerLayout: FrameLayout
    private lateinit var recyclerAdapter: ChipperRecyclerAdapter

    private val localViewModel: ChipperViewModel by lazy {
        ViewModelProviders.of(this).get(ChipperViewModel::class.java)
    }
    private val localTouchViewModel: ChipperTouchViewModel by lazy {
        ViewModelProviders.of(this).get(ChipperTouchViewModel::class.java)
    }

    //Writes/draws onto an assigned bitmap
    private lateinit var parentCanvas: Canvas
    //Aspect ratio Rect frame for subject image
    private val parentImageFrame: Rect by lazy { Rect() }

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    //Current touch action
    private var surfaceAction: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TODO("Have this converted to a Service (look at the latest saved bookmarks for info); figure that the Chipper" +
                "will be a transparent SurfaceView spawned over any selected image... What this means is that this will not go" +
                "into the NavHost and instead have its own standalone functionality (which is good)")

        activity?.let {
            globalViewModel = ViewModelProviders.of(it).get(GlobalViewModel::class.java)

            recyclerAdapter = ChipperRecyclerAdapter(it, this@ChipperFragment)
        }

        if(localViewModel.getParentId() != passedArgs.parentId)
            localViewModel.setParentId(passedArgs.parentId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.chipper_layout, container, false)

        chipperView = view.chipperSurfaceView.apply {
            setHandler(this@ChipperFragment)
            setOnTouchListener(this@ChipperFragment)
        }

        chipHolder = chipperView.holder

        recyclerLayout = view.chipperChildrenLayout

        recyclerVisBtn = view.chipperBtnRecyclerVis.apply {
            setOnTouchListener(this@ChipperFragment)
        }

        view.chipperRecycler.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        localViewModel.getParent()?.observe(this, Observer { parent ->
            Log.i("Life Event", "ChipperFragment#parentObserver: triggered!")

//            localViewModel.checkUpNavigation()

            localViewModel.setBitmap(parent.imgLocation)
            setBitmapRect()
        })

        localViewModel.getChildren()?.observe(this, Observer { children ->
            recyclerAdapter.setTopics(children)

            chipperView.invalidate()
        })

        localTouchViewModel.pathCreated.observe(this, Observer { created ->
            TODO()
        })

        return view
    }

    //Toggle the visibility of recyclerView displaying children and the image of the button responsible for toggling
    fun toggleRecyclerVis() {

        if(recyclerAdapter.itemCount == 0) return

        recyclerLayout.visibility =
            if(recyclerLayout.isVisible) {
                recyclerVisBtn.setImageResource(R.drawable.ic_arrow_web_recycler_show)
                View.GONE

            } else {
                recyclerVisBtn.setImageResource(R.drawable.ic_arrow_web_recycler_hide)
                View.VISIBLE
            }
    }


    //TODO (FUTURE): have this run on a separate thread, display loading bar
    private fun drawBackground() {

        if(localViewModel.getBitmap() == null)
            return

        localTouchViewModel.initPaint()

        parentCanvas = Canvas(localViewModel.getBitmap()!!.copy(Bitmap.Config.ARGB_8888, true))

        //Draw static images (eg. background, chip pathways)
        try {
            parentCanvas = chipHolder.lockCanvas(null)

            synchronized(chipHolder) {
                //TODO (FUTURE): load in a default bitmap if this one cannot be loaded
                parentCanvas.drawBitmap(localViewModel.getBitmap()!!, null, parentImageFrame, null)
            }
            //TODO: handle error
        } catch (e: Throwable) {
            e.printStackTrace()

        } finally {
            chipHolder.unlockCanvasAndPost(parentCanvas)
            //Reset flag
            localViewModel.backgroundChanged = false
        }
    }

    override fun drawScreen(canvas: Canvas) {

        if(localViewModel.backgroundChanged)
            drawBackground()

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

        val width = localViewModel.getBitmapWidth()
        val height = localViewModel.getBitmapHeight()

        if(width == 0 || height == 0)
            return

        parentImageFrame.set(
            RenderUtil.getAspectRatioRect(
                localViewModel.getBitmapWidth(), localViewModel.getBitmapHeight(), viewWidth, viewHeight))
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
                TODO()
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

    override fun chipTouch(id: Long) {

    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        if(event == null || event.action == MotionEvent.ACTION_DOWN)
            return false

        when(view?.id) {
            R.id.chipperBtnRecyclerVis -> {

                toggleRecyclerVis()
            }
        }

        return false
    }
}