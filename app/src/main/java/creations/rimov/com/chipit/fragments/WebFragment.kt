package creations.rimov.com.chipit.fragments

import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.adapters.WebRecyclerAdapter
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.util.handlers.RecyclerHandler
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.view_models.WebViewModel
import java.io.IOException

class WebFragment : Fragment(), View.OnClickListener, RecyclerHandler {

    object Constant {
        const val HORIZONTAL_CHIP_LIST = 0
        const val VERTICAL_CHIP_LIST = 1
    }

    private lateinit var globalViewModel: GlobalViewModel

    private val webViewModel: WebViewModel by lazy {
        ViewModelProviders.of(this).get(WebViewModel::class.java)
    }

    //Passed Bundle from DirectoryFragment
    private val passedArgs by navArgs<WebFragmentArgs>()

    private lateinit var hWebRecyclerAdapter: WebRecyclerAdapter
    private lateinit var vWebRecyclerAdapter: WebRecyclerAdapter

    private lateinit var gestureDetector: GestureDetector

    private var chipTouchId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webViewModel.initChips(passedArgs.parentId)

        activity?.let {
            globalViewModel = ViewModelProviders.of(it).get(GlobalViewModel::class.java)

            hWebRecyclerAdapter = WebRecyclerAdapter(it, Constant.HORIZONTAL_CHIP_LIST, this@WebFragment)
            vWebRecyclerAdapter = WebRecyclerAdapter(it, Constant.VERTICAL_CHIP_LIST, this@WebFragment)
        }

        gestureDetector = GestureDetector(activity, ChipGestureDetector())
        gestureDetector.setIsLongpressEnabled(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.web_layout, container, false)

        val addChipPanelLayout: LinearLayout = view.findViewById(R.id.web_layout_addpanel)
        //TODO: create a custom button
        val addChipCameraButton: ImageButton = view.findViewById(R.id.web_layout_addpanel_camera)
        val addChipFilesButton: ImageButton = view.findViewById(R.id.web_layout_addpanel_files)

        //Horizontal recycler view for "sibling" children
        val hChipRecyclerView = view.findViewById<RecyclerView>(R.id.web_layout_recycler_h_chips).apply {
            adapter = hWebRecyclerAdapter
            layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
        }
        //Vertical recycler view for "children" children
        val vChipRecyclerView = view.findViewById<RecyclerView>(R.id.web_layout_recycler_v_chips).apply {
            adapter = vWebRecyclerAdapter
            layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
        }

        globalViewModel.fabTouched.observe(this, Observer { touched ->

            if(touched) {
                addChipPanelLayout.visibility = View.VISIBLE

            } else {
                addChipPanelLayout.visibility = View.GONE
            }
        })

        webViewModel.getChipsHorizontal()?.observe(this, Observer { chips ->
            hWebRecyclerAdapter.setChips(chips)
        })

        webViewModel.getChipsVertical()?.observe(this, Observer { chips ->
            vWebRecyclerAdapter.setChips(chips)
        })

        webViewModel.chipTouch.observe(this, Observer { flag ->

            if(chipTouchId == -1L)
                return@Observer

            when {
                flag.chipTouched -> {
                    val directions = WebFragmentDirections.actionWebFragmentToChipFragment(chipTouchId)
                    findNavController().navigate(directions)
                }

                flag.chipLongTouched -> {
                    //TODO FUTURE: implement long touch feature
                }
            }
        })

        addChipCameraButton.setOnClickListener(this)
        addChipFilesButton.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View?) {

        when(view?.id) {

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
                        webViewModel.saveChip("", imageFile.storagePath)
                }
            }

            R.id.web_layout_addpanel_files -> {

            }
        }
    }

    override fun topicTouch(position: Int, event: MotionEvent, listType: Int) {
        gestureDetector.onTouchEvent(event)

        chipTouchId = webViewModel.getChipAtPosition(listType, position)?.id ?: -1L
    }

    //According to developer website, must override onDown to return true to ensure gestures are not ignored
    inner class ChipGestureDetector : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(event: MotionEvent?): Boolean {
            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {
            webViewModel.setChipTouch()

            return super.onSingleTapUp(event)
        }

        override fun onLongPress(event: MotionEvent?) {
            webViewModel.setChipLongTouch()

        }
    }
}