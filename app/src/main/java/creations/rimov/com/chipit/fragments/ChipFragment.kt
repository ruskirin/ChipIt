package creations.rimov.com.chipit.fragments

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.objects.Point
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.util.RenderUtil
import creations.rimov.com.chipit.view_models.ChipTouchViewModel
import creations.rimov.com.chipit.view_models.ChipViewModel
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.views.ChipView
import java.io.IOException

class ChipFragment : Fragment(), ChipView.ChipListener, View.OnTouchListener {

    private object Constant {
        //Buffer around surfaceview edge to trigger gesture swipe events
        const val EDGE_TOUCH_BUFFER = 5f
    }

    private lateinit var globalViewModel: GlobalViewModel

    //Passed Bundle from DirectoryFragment
    private val passedArgs by navArgs<ChipFragmentArgs>()

    private lateinit var chipView: ChipView
    private lateinit var chipHolder: SurfaceHolder

    private val chipViewModel: ChipViewModel by lazy {
        ViewModelProviders.of(this).get(ChipViewModel::class.java)
    }
    private val chipTouchViewModel: ChipTouchViewModel by lazy {
        ViewModelProviders.of(this).get(ChipTouchViewModel::class.java)
    }

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

    private var screenW: Int = 0
    private var screenH: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            globalViewModel = ViewModelProviders.of(it).get(GlobalViewModel::class.java)
        }

        chipViewModel.setParent(passedArgs.parentId)

        chipViewModel.getParent()?.observe(this, Observer { parent ->

            if(parent.imgLocation != chipViewModel.getImagePath()) {
                chipViewModel.setBitmap(parent.imgLocation)
                setBitmapRect()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.chip_layout, container, false)

        chipView = view.findViewById(R.id.chip_layout_surfaceview)
        chipHolder = chipView.holder

        val pathPanelLayout: LinearLayout = view.findViewById(R.id.chip_layout_pathpanel)
        val pathPanelCamera: ImageButton = view.findViewById(R.id.chip_layout_pathpanel_camera)
        val pathPanelPhotos: ImageButton = view.findViewById(R.id.chip_layout_pathpanel_photos)
        val pathPanelCancel: ImageButton = view.findViewById(R.id.chip_layout_pathpanel_cancel)

        chipView.setListener(this)
        chipView.setOnTouchListener(this)

        pathPanelCamera.setOnTouchListener(this)
        pathPanelPhotos.setOnTouchListener(this)
        pathPanelCancel.setOnTouchListener(this)

        chipViewModel.getChildren().observe(this, Observer {
            chipView.invalidate()
        })

        chipTouchViewModel.pathCreated.observe(this, Observer { created ->

            if(created) {
                pathPanelLayout.visibility = View.VISIBLE

            } else {
                pathPanelLayout.visibility = View.GONE
            }
        })

        return view
    }


    //TODO (FUTURE): have this run on a separate thread, display loading bar
    private fun drawBackground() {

        if(chipViewModel.getBitmap() == null)
            return

        initPaint()

        parentCanvas = Canvas(chipViewModel.getBitmap()!!.copy(Bitmap.Config.ARGB_8888, true))

        //Draw static images (eg. background, chip pathways)
        try {
            parentCanvas = chipHolder.lockCanvas(null)

            synchronized(chipHolder) {
                //TODO (FUTURE): load in a default bitmap if this one cannot be loaded
                parentCanvas.drawBitmap(chipViewModel.getBitmap()!!, null, parentImageFrame, null)
            }
            //TODO: handle error
        } catch (e: Throwable) {
            e.printStackTrace()

        } finally {
            chipHolder.unlockCanvasAndPost(parentCanvas)
            //Reset flag
            chipViewModel.backgroundChanged = false
        }
    }

    override fun drawScreen(canvas: Canvas) {

        if(chipViewModel.backgroundChanged)
            drawBackground()

        canvas.drawPath(chipPath, chipPaint)

        drawChildren(canvas)
    }

    private fun drawChildren(canvas: Canvas) {

        if(screenW == 0 || screenH == 0 || parentImageFrame.width() == 0 || parentImageFrame.height() == 0)
            return

        //TODO (FUTURE): should be off UI thread
        chipViewModel.getChildren().value?.forEach { chip ->

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

        chipViewModel.backgroundChanged = true
    }

    override fun setBitmapRect() {

        val width = chipViewModel.getBitmapWidth()
        val height = chipViewModel.getBitmapHeight()

        if(width == 0 || height == 0)
            return

        parentImageFrame.set(
            RenderUtil.getAspectRatioRect(
                chipViewModel.getBitmapWidth(), chipViewModel.getBitmapHeight(), screenW, screenH))
    }

    override fun surfaceTouch(event: MotionEvent) {

    }

    //TODO: (FUTURE) should not be able to draw outside background rectangle
    override fun chipStart(x: Float, y: Float) {

        chipPath.moveTo(x, y)

        chipTouchViewModel.startPath(Point(x, y))
    }

    override fun chipDrag(x: Float, y: Float) {

        if(chipTouchViewModel.dragPath(Point(x, y)))
            chipPath.lineTo(x, y)
    }

    override fun chipEnd(x: Float, y: Float) {

        if(chipTouchViewModel.endPath(
                Point(x, y), screenW, screenH, parentImageFrame.width(), parentImageFrame.height()))
            chipPath.lineTo(x, y)

        //Either path has been saved or was incomplete, regardless it is no longer necessary
        clearPaths(chipPath)
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        when(view?.id) {
            R.id.chip_layout_surfaceview -> {

                chipTouchViewModel.gestureAction(event?.action ?: -1, Point(event!!.rawX, event.rawY))
            }
            R.id.chip_layout_pathpanel_camera -> {

                if(event?.action != MotionEvent.ACTION_DOWN)
                    return false

                val addChipCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                //Verifies that an application that can handle this intent exists
                addChipCameraIntent.resolveActivity(activity!!.packageManager)

                //TODO: handle error
                val imageFile = try {
                    CameraUtil.createImageFile(activity!!)

                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }

                if (imageFile != null) {
                    val imageUri = FileProvider.getUriForFile(
                        activity!!,
                        CameraUtil.IMAGE_PROVIDER_AUTHORITY,
                        imageFile.file
                    )

                    addChipCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(addChipCameraIntent, CameraUtil.CODE_TAKE_PICTURE)

                    //TODO (FUTURE): verify that the id exists (here or elsewhere)
                    if (imageFile.storagePath.isNotEmpty()) {
                        chipViewModel.saveChip(null, imageFile.storagePath, chipTouchViewModel.getPathVertices())
                        //Toggle flag
                        chipTouchViewModel.pathCreated.postValue(false)

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

                chipTouchViewModel.pathCreated.postValue(false)
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