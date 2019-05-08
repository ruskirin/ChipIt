package creations.rimov.com.chipit.activities

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.objects.Point
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.view_models.ChipViewModel
import creations.rimov.com.chipit.util.RenderUtil
import creations.rimov.com.chipit.views.ChipView
import java.io.IOException

class ChipActivity : AppCompatActivity(), ChipView.ChipListener, View.OnTouchListener {

    private val chipView: ChipView by lazy {
        findViewById<ChipView>(R.id.chip_layout_surfaceview)
    }

    private val pathPanelLayout: LinearLayout by lazy {
        findViewById<LinearLayout>(R.id.chip_layout_pathpanel)
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

        viewModel.getParent()?.observe(this, Observer { parent ->

            if(parent.imgLocation != viewModel.getImagePath()) {
                Log.i("ChipActivity", "#onCreate(): setting bitmap from ${parent.imgLocation}")

                viewModel.setBitmap(parent.imgLocation)
            }
        })

        viewModel.getChildren().observe(this, Observer {
            chipView.invalidate()
        })

        viewModel.pathCreated.observe(this, Observer { created ->

            if(created) {
                pathPanelLayout.visibility = View.VISIBLE

            } else {
                pathPanelLayout.visibility = View.GONE
            }
        })

        val pathPanelCamera: ImageButton = findViewById(R.id.chip_layout_pathpanel_camera)
        val pathPanelPhotos: ImageButton = findViewById(R.id.chip_layout_pathpanel_photos)
        val pathPanelCancel: ImageButton = findViewById(R.id.chip_layout_pathpanel_cancel)

        pathPanelCamera.setOnTouchListener(this)
        pathPanelPhotos.setOnTouchListener(this)
        pathPanelCancel.setOnTouchListener(this)
    }


    //TODO (FUTURE): have this run on a separate thread, display loading bar
    private fun drawBackground() {

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

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        when(view?.id) {
            R.id.chip_layout_surfaceview -> {

                when(event?.action) {
                    MotionEvent.ACTION_DOWN -> {

                        //TODO (NOW): set up a flag in VM that will trigger visibility of an "editor" FAB
                        if(event.rawX )
                    }
                    MotionEvent.ACTION_MOVE -> {

                    }
                    MotionEvent.ACTION_UP -> {

                    }
                }
            }
            R.id.chip_layout_pathpanel_camera -> {

                if(event?.action != MotionEvent.ACTION_DOWN)
                    return false

                val addChipCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                //Verifies that an application that can handle this intent exists
                addChipCameraIntent.resolveActivity(packageManager)

                //TODO: handle error
                val imageFile = try {
                    CameraUtil.createImageFile(this)

                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }

                if (imageFile != null) {
                    val imageUri = FileProvider.getUriForFile(
                        this,
                        CameraUtil.IMAGE_PROVIDER_AUTHORITY,
                        imageFile.file
                    )

                    addChipCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(addChipCameraIntent, CameraUtil.CODE_TAKE_PICTURE)

                    //TODO (FUTURE): verify that the id exists (here or elsewhere)
                    if (imageFile.storagePath.isNotEmpty()) {
                        viewModel.saveChip(null, imageFile.storagePath, viewModel.getPathVertices())
                        //Toggle flag
                        viewModel.pathCreated.postValue(false)

                        Log.i("Chip Creation",
                            "ChipActivity#onTouch(): new chip inserted! Image location: ${imageFile.storagePath}")
                    }

                    return true
                }
            }
            R.id.chip_layout_pathpanel_photos -> {

                if(event?.action != MotionEvent.ACTION_DOWN)
                    return false

                return true
            }
            R.id.chip_layout_pathpanel_cancel -> {

                if(event?.action != MotionEvent.ACTION_DOWN)
                    return false

                viewModel.pathCreated.postValue(false)
                return true
            }
        }

        return false
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

