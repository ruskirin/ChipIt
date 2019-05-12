package creations.rimov.com.chipit.fragments

import android.content.Intent
import android.media.Image
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.activities.ChipActivity
import creations.rimov.com.chipit.adapters.WebRecyclerAdapter
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.util.handlers.RecyclerHandler
import creations.rimov.com.chipit.view_models.WebViewModel
import java.io.IOException

class WebFragment : Fragment(), View.OnClickListener, RecyclerHandler {

    object Constant {
        const val HORIZONTAL_CHIP_LIST = 0
        const val VERTICAL_CHIP_LIST = 1
    }

    private lateinit var viewModel: WebViewModel

    //Horizontal recycler view for "sibling" children
    private lateinit var hChipRecyclerView: RecyclerView
    private lateinit var hWebRecyclerAdapter: WebRecyclerAdapter
    private lateinit var hChipLayoutManager: LinearLayoutManager
    //Vertical recycler view for "children" children
    private lateinit var vChipRecyclerView: RecyclerView
    private lateinit var vWebRecyclerAdapter: WebRecyclerAdapter
    private lateinit var vChipLayoutManager: LinearLayoutManager

    private lateinit var addChipFab: FloatingActionButton
    private lateinit var addChipPanelLayout: LinearLayout

    private lateinit var gestureDetector: GestureDetector

    private var chipPressed = false
    private var chipLongPressed = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO NOW: set up a shared viewmodel between activity and this and then instantiate the recyclerviews

        activity?.run {
            ViewModelProviders.of(this).get(WebViewModel::class.java)

            hWebRecyclerAdapter = WebRecyclerAdapter(this, Constant.HORIZONTAL_CHIP_LIST, this@WebFragment)
            hChipLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

            vWebRecyclerAdapter = WebRecyclerAdapter(this, Constant.VERTICAL_CHIP_LIST, this@WebFragment)
            vChipLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }

        gestureDetector = GestureDetector(activity, ChipGestureDetector())
        gestureDetector.setIsLongpressEnabled(true)

        viewModel.getChipsHorizontal()?.observe(this, Observer { chips ->
            hWebRecyclerAdapter.setChips(chips)
        })

        viewModel.getChipsVertical()?.observe(this, Observer { chips ->
            vWebRecyclerAdapter.setChips(chips)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.web_layout, container, false)

        addChipFab = view.findViewById(R.id.web_layout_fab_add)
        addChipPanelLayout = view.findViewById(R.id.web_layout_addpanel)

        //TODO: create a custom button
        val addChipCameraButton: ImageButton = view.findViewById(R.id.web_layout_addpanel_camera)
        val addChipFilesButton: ImageButton = view.findViewById(R.id.web_layout_addpanel_files)

        hChipRecyclerView = view.findViewById<RecyclerView>(R.id.web_layout_recycler_h_chips).apply {
            adapter = hWebRecyclerAdapter
            layoutManager = hChipLayoutManager
            setHasFixedSize(true)
        }
        vChipRecyclerView = view.findViewById<RecyclerView>(R.id.web_layout_recycler_v_chips).apply {
            adapter = vWebRecyclerAdapter
            layoutManager = vChipLayoutManager
            setHasFixedSize(true)
        }

        addChipFab.setOnClickListener(this)
        addChipCameraButton.setOnClickListener(this)
        addChipFilesButton.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View?) {

        when(view?.id) {

            R.id.web_layout_fab_add -> {

                if(addChipPanelLayout.visibility == View.GONE) {
                    addChipPanelLayout.visibility = View.VISIBLE
                    addChipFab.hide()
                }
            }
            R.id.web_layout_addpanel_camera -> {
                val addChipCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                //Verifies that an application that can handle this intent exists
                addChipCameraIntent.resolveActivity(activity!!.packageManager)

                //TODO: handle error
                val imageFile = try {
                    CameraUtil.createImageFile(activity!!)

                } catch(e: IOException) {
                    e.printStackTrace()
                    null
                }

                if(imageFile != null) {
                    val imageUri = FileProvider.getUriForFile(activity!!,
                        CameraUtil.IMAGE_PROVIDER_AUTHORITY,
                        imageFile.file)

                    addChipCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(addChipCameraIntent, CameraUtil.CODE_TAKE_PICTURE)

                    if(imageFile.storagePath.isNotEmpty())
                        viewModel.saveChip("", imageFile.storagePath)
                }
            }

            R.id.web_layout_addpanel_files -> {

            }
        }
    }

    override fun topicTouch(position: Int, event: MotionEvent, listType: Int) {
        gestureDetector.onTouchEvent(event)

        if(chipPressed) {
            val toChip = Intent(activity!!, ChipActivity::class.java)

            val id = viewModel.getChipAtPosition(listType, position)?.id

            if(id != null) {
                Log.i("Chip Creation", "Passed chip $id")

                toChip.putExtra("chip_id", id)
                startActivity(toChip)

            } else {
                Log.e("WebActivity", "#topicTouch: chip at position $position could not be found")
                //TODO: handle this scenario
            }

            //Reset flag
            chipPressed = false
        }
    }

    //According to developer website, must override onDown to return true to ensure gestures are not ignored
    inner class ChipGestureDetector : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(event: MotionEvent?): Boolean {
            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {
            chipPressed = true

            return super.onSingleTapUp(event)
        }

        override fun onLongPress(event: MotionEvent?) {
            chipLongPressed = true
            chipPressed = false

        }
    }
}