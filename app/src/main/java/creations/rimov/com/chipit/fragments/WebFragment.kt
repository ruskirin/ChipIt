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
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.util.WebRecyclerItemDecoration
import creations.rimov.com.chipit.util.handlers.RecyclerTouchHandler
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.view_models.WebViewModel
import java.io.IOException

class WebFragment : Fragment(), View.OnClickListener, RecyclerTouchHandler {

    object Constants {
        const val LIST_UPPER = 100
        const val LIST_LOWER = 200

        const val GESTURE_TOUCH = 400
        const val GESTURE_LONG_TOUCH = 401
    }

    private lateinit var globalViewModel: GlobalViewModel

    private val localViewModel: WebViewModel by lazy {
        ViewModelProviders.of(this).get(WebViewModel::class.java)
    }

    //Passed Bundle from DirectoryFragment
    private val passedArgs by navArgs<WebFragmentArgs>()

    private lateinit var uRecyclerAdapter: WebRecyclerAdapter
    private lateinit var lRecyclerAdapter: WebRecyclerAdapter

    private lateinit var gestureDetector: GestureDetector

    //Holds reference to the chips displayed in the horizontal recyclerview
    private lateinit var uChips: List<ChipCard>

    private var chipTouchId: Long = -1L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            globalViewModel = ViewModelProviders.of(it).get(GlobalViewModel::class.java)

            uRecyclerAdapter = WebRecyclerAdapter(it, Constants.LIST_UPPER, this@WebFragment)
            lRecyclerAdapter = WebRecyclerAdapter(it, Constants.LIST_LOWER, this@WebFragment)

            gestureDetector = GestureDetector(it, ChipGestureDetector())
            gestureDetector.setIsLongpressEnabled(true)
        }

        if(passedArgs.parentId != -1L)
            localViewModel.setUpperList(passedArgs.parentId)
        else
            localViewModel.setUpperList(globalViewModel.chipFragParentId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.web_layout, container, false)

        val addChipPanelLayout: LinearLayout = view.findViewById(R.id.web_layout_addpanel)
        //TODO: create a custom button
        val addChipCameraButton: ImageButton = view.findViewById(R.id.web_layout_addpanel_camera)
        val addChipFilesButton: ImageButton = view.findViewById(R.id.web_layout_addpanel_files)

        val recyclerItemDecoration = WebRecyclerItemDecoration(-1)

        //Horizontal recycler view for "sibling" children
        val uRecyclerView = view.findViewById<RecyclerView>(R.id.web_layout_recycler_u_chips).apply {
            adapter = uRecyclerAdapter
            layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
            addItemDecoration(recyclerItemDecoration)
            setHasFixedSize(true)
        }
        //Vertical recycler view for "children" children
        val lRecyclerView = view.findViewById<RecyclerView>(R.id.web_layout_recycler_l_chips).apply {
            adapter = lRecyclerAdapter
            layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        globalViewModel.fabTouched.observe(this, Observer { touched ->

            if(touched) {
                addChipPanelLayout.visibility = View.VISIBLE

            } else {
                addChipPanelLayout.visibility = View.GONE
            }
        })

        localViewModel.getListUpper()?.observe(this, Observer { chips ->
            uRecyclerAdapter.setChips(chips)

            uChips = chips
        })

        localViewModel.getListLower()?.observe(this, Observer { chips ->
            Log.i("RecyclerView", "Observer: setting chips")

            lRecyclerAdapter.setChips(chips)
        })

        localViewModel.prompts.observe(this, Observer { prompt ->

            if(chipTouchId == -1L)
                return@Observer

            when {
                prompt.highlightChip -> {

                    recyclerItemDecoration.setSelectedItem(chipTouchAdapterPos)
                    localViewModel.setLowerList(uChips[chipTouchAdapterPos].id)
                }

                prompt.toChipFrag -> {

                    val directions = WebFragmentDirections.actionWebFragmentToChipFragment(chipTouchId)
                    findNavController().navigate(directions)

                    globalViewModel.saveChipFragParentId(localViewModel.getParentId())
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
                        localViewModel.saveChip("", imageFile.storagePath)
                }
            }

            R.id.web_layout_addpanel_files -> {

            }
        }
    }

    override fun topicTouch(position: Int, chipId: Long, event: MotionEvent, listType: Int) {
        gestureDetector.onTouchEvent(event)


    }

    inner class ChipGestureDetector : GestureDetector.SimpleOnGestureListener() {

        //According to developer website, must return true to ensure gestures are not ignored
        override fun onDown(event: MotionEvent?): Boolean {
            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {
            localViewModel.handleChipGesture(Constants.GESTURE_TOUCH)

            return super.onSingleTapUp(event)
        }

        override fun onLongPress(event: MotionEvent?) {
            localViewModel.handleChipGesture(Constants.GESTURE_LONG_TOUCH)
        }
    }
}