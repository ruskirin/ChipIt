package creations.rimov.com.chipit.activities

import android.graphics.*
import android.os.Bundle
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.objects.Point
import creations.rimov.com.chipit.view_models.ChipViewModel
import creations.rimov.com.chipit.util.RenderUtil
import creations.rimov.com.chipit.views.ChipView
import kotlin.math.sqrt

class ChipActivity : AppCompatActivity(), ChipView.ChipListener {

    object Constant {
        //Radius around path starting point which will autosnap an endpoint and complete the chip
        const val TOLERANCE = 100f
        //Minimum distance from a path vertex before another vertex is created and connected
        const val LINE_INTERVAL = 50f
    }

    private val chipView: ChipView by lazy {
        findViewById<ChipView>(R.id.chip_layout_surfaceview)
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
    private lateinit var subjectCanvas: Canvas

    //Aspect ratio Rect frame for subject image
    private val subjectImageFrame: Rect by lazy {
        Rect()
    }

    //Class encapsulating geometric paths; used for vertex mapping
    private val chipPath = Path()
    //Class responsible for assigning color to vertices
    private val chipPaint = Paint()

    //Path starting points
    private var pathX0 = 0f
    private var pathY0 = 0f
    //Path previous coordinates
    private var pathX = 0f
    private var pathY = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chip_layout)

        if(viewModel.getSubject().imagePath == "") {

            val parcel = intent?.extras
            if (parcel != null) {

                if (!viewModel.setSubject(parcel.getParcelable("chip"))) {
                    Toast.makeText(
                        this, "Could not retrieve intent extras; subject not set", Toast.LENGTH_LONG
                    ).show()

                    /**TODO: (FUTURE) display appropriate image and stop further processes **/
                }
            }
        }

        chipView.setListener(this)
    }

    /*----------CHIPLISTENER IMPLEMENTATION----------*/
    override fun drawScreen(canvas: Canvas) {

        canvas.drawPath(chipPath, chipPaint)

        drawChildren(canvas)
    }

    override fun initSurface() {

        initPaint()

        //Draw static images (eg. background, chip pathways)
        try {
            subjectCanvas = chipHolder.lockCanvas(null)
            synchronized(chipHolder) {
                subjectCanvas.drawBitmap(viewModel.getSubjectBitmap(), null, subjectImageFrame, null)
            }
            //TODO: handle error
        } catch(e: Throwable) {
            e.printStackTrace()

        } finally {
            chipHolder.unlockCanvasAndPost(subjectCanvas)
        }
    }

    override fun setScreenDimen(width: Int, height: Int) {
        screenW = width
        screenH = height
    }

    override fun setSubjectRect() {
        subjectImageFrame.set(
            RenderUtil.getAspectRatioRect(
                viewModel.getSubjectBitmapWidth(), viewModel.getSubjectBitmapHeight(), screenW, screenH))
    }

    //TODO: (FUTURE) should not be able to draw outside background rectangle
    override fun chipStart(x: Float, y: Float) {
        //TODO: add an image path after one has been assigned
        chipPath.moveTo(x, y)
        //set initial point
        pathX0 = x
        pathY0 = y
        //save as previous point
        pathX = x
        pathY = y

        viewModel.initChip()
        viewModel.addChipVertex(RenderUtil.pointToNorm(Point(x, y), screenW, screenH))
    }

    override fun chipDrag(x: Float, y: Float) {
        val dx = x - pathX
        val dy = y - pathY
        val d = sqrt(dx*dx + dy*dy)

        if(d > Constant.LINE_INTERVAL) {
            chipPath.lineTo(x, y)
            pathX = x
            pathY = y

            //TODO: consider doing this conversion and save after drawing is done; can convert a whole array instead
            viewModel.addChipVertex(RenderUtil.pointToNorm(Point(x, y), screenW, screenH))
        }
    }

    override fun chipEnd(x: Float, y: Float) {
        val distance = sqrt((x - pathX0)*(x - pathX0) + (y - pathY0)*(y - pathY0))

        if(distance <= Constant.TOLERANCE) {
            chipPath.lineTo(pathX0, pathY0)

            viewModel.addChipVertex(RenderUtil.pointToNorm(Point(pathX0, pathY0), screenW, screenH))
            //Save chip in main subject's children list
            viewModel.saveChip()
            //Reset object references
            viewModel.initChip()
        }

        clearPaths(chipPath)
    }
    /*-------------------------------------------*/

    private fun drawChildren(canvas: Canvas) {

        viewModel.getSubject().children.forEach {
            canvas.drawLines(
                it.getVerticesFloatArray(
                    true, true, screenW, screenH, subjectImageFrame.width(), subjectImageFrame.height()),
                chipPaint)
        }
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

