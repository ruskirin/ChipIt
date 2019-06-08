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
import creations.rimov.com.chipit.database.objects.ChipPath
import creations.rimov.com.chipit.objects.CoordPoint
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.util.RenderUtil
import creations.rimov.com.chipit.view_models.ChipTouchViewModel
import creations.rimov.com.chipit.view_models.ChipViewModel
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.views.ChipView
import java.io.IOException

class ChipFragment : Fragment(), ChipView.ChipHandler, View.OnTouchListener {

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
    private val passedArgs by navArgs<ChipFragmentArgs>()

    private lateinit var chipView: ChipView
    private lateinit var chipHolder: SurfaceHolder

    private val localViewModel: ChipViewModel by lazy {
        ViewModelProviders.of(this).get(ChipViewModel::class.java)
    }
    private val localTouchViewModel: ChipTouchViewModel by lazy {
        ViewModelProviders.of(this).get(ChipTouchViewModel::class.java)
    }

    //Writes/draws onto an assigned bitmap
    private lateinit var parentCanvas: Canvas
    //Aspect ratio Rect frame for subject image
    private val parentImageFrame: Rect by lazy {
        Rect()
    }

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    //Current touch action
    private var surfaceAction: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            globalViewModel = ViewModelProviders.of(it).get(GlobalViewModel::class.java)
        }

        localViewModel.setParent(passedArgs.parentId)

        globalViewModel.getUpFlag().observe(this, Observer { flag ->
            //Navigate up to the parent of the currently displayed chip
            if(flag.touched) {
                val id = localViewModel.getParentIdOfParent()

                localViewModel.setParent(id)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.chip_layout, container, false)

        chipView = view.findViewById(R.id.chip_layout_surfaceview)
        chipHolder = chipView.holder

        val childObserver: Observer<List<ChipPath>> = Observer {
            chipView.invalidate()
        }

        val pathPanelLayout: LinearLayout = view.findViewById(R.id.chip_layout_pathpanel)
        val pathPanelCamera: ImageButton = view.findViewById(R.id.chip_layout_pathpanel_camera)
        val pathPanelPhotos: ImageButton = view.findViewById(R.id.chip_layout_pathpanel_photos)
        val pathPanelCancel: ImageButton = view.findViewById(R.id.chip_layout_pathpanel_cancel)

        chipView.setHandler(this)
        chipView.setOnTouchListener(this)

        pathPanelCamera.setOnTouchListener(this)
        pathPanelPhotos.setOnTouchListener(this)
        pathPanelCancel.setOnTouchListener(this)

        localViewModel.getParent()?.observe(this, Observer { parent ->
            Log.i("Life Event", "ChipFragment#parentObserver: triggered!")

            localViewModel.setBitmap(parent.imgLocation)
            setBitmapRect()
            //Children changed with parent, replace old observer with new one
            localViewModel.getChildren()?.removeObserver(childObserver)
            localViewModel.getChildren()?.observe(this, childObserver)
        })

        localTouchViewModel.pathCreated.observe(this, Observer { created ->

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

        viewWidth = chipView.measuredWidth
        viewHeight = chipView.measuredHeight

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

            Log.i("Touch Event", "ChipFragment#setAction(): touched chip: ${touchedChip.id}")

            return
        }

        surfaceAction = Constant.DRAW_PATH
    }

    //TODO: (FUTURE) should not be able to draw outside background rectangle
    override fun touchDown(x: Float, y: Float) {
        globalViewModel.displayFab(false)
        globalViewModel.displayUp(false)

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
                if((point0.y - y) > Constant.SWIPE_BUFFER) {
                    globalViewModel.displayFab(true)

                    if(!localViewModel.isParentOfParentTopic())
                        globalViewModel.displayUp(true)

                } else {
                    globalViewModel.displayFab(false)
                    globalViewModel.displayUp(false)
                }
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
                    localViewModel.setParent(touchedChip.id)
            }
        }
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        when(view?.id) {
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
                        localViewModel.saveChip(null, imageFile.storagePath, localTouchViewModel.getPathVertices())
                        //Toggle flag
                        localTouchViewModel.pathCreated.postValue(false)

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

                localTouchViewModel.pathCreated.postValue(false)
                return true
            }
        }

        return false
    }
}