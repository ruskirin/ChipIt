package creations.rimov.com.chipit.activities

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.objects.Point
import creations.rimov.com.chipit.view_models.ChipViewModel
import creations.rimov.com.chipit.util.RenderUtil
import creations.rimov.com.chipit.util.TextureUtil
import creations.rimov.com.chipit.views.ChipView

class ChipActivity : AppCompatActivity(), ChipView.ChipListener {

    private val chipView: ChipView by lazy {
        findViewById<ChipView>(R.id.chip_layout_surfaceview)
    }

    //TODO NOW: the layout for the path creation panel was created, now it must be displayed once a path is created;
    //           figure out the best approach to having it displayed
    private val pathPanelLayout: LinearLayout by lazy {
        findViewById<LinearLayout>(R.id.chip_layout_pathpanel)
    }
    private val pathPanelCamera: ImageButton by lazy {
        findViewById<ImageButton>(R.id.chip_layout_pathpanel_camera)
    }
    private val pathPanelPhotos: ImageButton by lazy {
        findViewById<ImageButton>(R.id.chip_layout_pathpanel_photos)
    }


    private val chipHolder: SurfaceHolder by lazy {
        chipView.holder
    }

    private val viewModel: ChipViewModel by lazy {
        ViewModelProviders.of(this).get(ChipViewModel::class.java)
    }


    private var screenW: Int = 0
    private var screenH: Int = 0

    //Writes/draws onto an assigned bitmap
    private lateinit var parentCanvas: Canvas
    //Aspect ratio Rect frame for subject image
    private val parentImageFrame: Rect by lazy {
        Rect()
    }
    //Class encapsulating geometric paths; used for vertex mapping
    private val chipPath = Path()
    //Class responsible for assigning color to vertices
    private val chipPaint = Paint()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chip_layout)

        chipView.setListener(this)

        Log.i("ChipActivity", "#onCreate()")

        val chipId = intent?.extras?.getLong("chip_id")

        if(chipId != null && !viewModel.isParentInit())
            viewModel.setParent(chipId)

        viewModel.getParent()?.observe(this, Observer {

            if(it.imagePath != viewModel.imagePath) {
                Log.i("ChipActivity", "#onCreate(): setting bitmap from ${it.imagePath}")

                viewModel.setBitmap(it.imagePath)
            }
        })

        viewModel.getChildren().observe(this, Observer {
            chipView.invalidate()
        })
    }


    //TODO (FUTURE): have this run on a separate thread, display loading bar
    fun drawBackground() {

        initPaint()

        parentCanvas = Canvas(viewModel.getBitmap()!!.copy(Bitmap.Config.ARGB_8888, true))

        //Draw static images (eg. background, chip pathways)
        try {
            parentCanvas = chipHolder.lockCanvas(null)

            synchronized(chipHolder) {
                //TODO (FUTURE): load in a default bitmap if this one cannot be loaded
                parentCanvas.drawBitmap(viewModel.getBitmap()!!, null, parentImageFrame, null)
            }
            //TODO: handle error
        } catch (e: Throwable) {
            e.printStackTrace()

        } finally {
            chipHolder.unlockCanvasAndPost(parentCanvas)
            //Reset flag
            viewModel.backgroundChanged = false
        }
    }

    override fun drawScreen(canvas: Canvas) {

        if(viewModel.backgroundChanged)
            drawBackground()

        canvas.drawPath(chipPath, chipPaint)

        drawChildren(canvas)
    }

    private fun drawChildren(canvas: Canvas) {

        if(screenW == 0 || screenH == 0 || parentImageFrame.width() == 0 || parentImageFrame.height() == 0)
            return

        //TODO (FUTURE): should be off UI thread
        viewModel.getChildren().value?.forEach { chip ->

            if(chip.vertices.isNullOrEmpty() || chip.vertices!!.size <= 3)
                return@forEach

            canvas.drawLines(
                chip.getVerticesFloatArray(
                    true, screenW, screenH, parentImageFrame.width(), parentImageFrame.height())!!,
                chipPaint)
        }
    }

    override fun setScreenDimen() {

        screenW = chipView.measuredWidth
        screenH = chipView.measuredHeight

        viewModel.backgroundChanged = true
    }

    override fun setBitmapRect(): Boolean {

        val width = viewModel.getBitmapWidth()
        val height = viewModel.getBitmapHeight()

        if(width == 0 || height == 0)
            return false

        parentImageFrame.set(
            RenderUtil.getAspectRatioRect(
                viewModel.getBitmapWidth(), viewModel.getBitmapHeight(), screenW, screenH))

        return true
    }

    //TODO: (FUTURE) should not be able to draw outside background rectangle
    override fun chipStart(x: Float, y: Float) {

        chipPath.moveTo(x, y)

        viewModel.startPath(Point(x, y))
    }

    override fun chipDrag(x: Float, y: Float) {

        if(viewModel.dragPath(Point(x, y)))
            chipPath.lineTo(x, y)
    }

    override fun chipEnd(x: Float, y: Float) {

        if(viewModel.endPath(
                Point(x, y), screenW, screenH, parentImageFrame.width(), parentImageFrame.height()))
            chipPath.lineTo(x, y)

        //Either path has been saved or was incomplete, regardless it is no longer necessary
        clearPaths(chipPath)
    }

    private fun initPaint() {

        chipPaint.apply {
            isAntiAlias = true
            color = Color.CYAN
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeWidth = 4f
        }
    }

    /**
     * Reset path objects
     */
    private fun clearPaths(vararg paths: Path) {
        paths.forEach {
            it.reset()
        }
    }
}

