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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.activities.DirectoryActivity
import creations.rimov.com.chipit.adapters.WebRecyclerAdapter
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.util.handlers.RecyclerTouchHandler
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.view_models.WebViewModel
import java.io.IOException

class WebFragment : Fragment(), View.OnClickListener, RecyclerTouchHandler {

    object ListType {
        const val UPPER = 100
        const val LOWER = 200
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            globalViewModel = ViewModelProviders.of(it).get(GlobalViewModel::class.java)

            uRecyclerAdapter = WebRecyclerAdapter(it, ListType.UPPER, this@WebFragment)
            lRecyclerAdapter = WebRecyclerAdapter(it, ListType.LOWER, this@WebFragment)

            gestureDetector = GestureDetector(it, ChipGestureDetector())
            gestureDetector.setIsLongpressEnabled(true)
        }

        Log.i("Life Event", "WebFragment#onCreate(): passed parent id: ${passedArgs.parentId}")

        when {
            localViewModel.getParentId() != -1L -> localViewModel.setParent(localViewModel.getParentId())

            passedArgs.parentId != -1L -> localViewModel.setParent(passedArgs.parentId)

            else -> localViewModel.setParent(globalViewModel.chipFragParentId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.web_layout, container, false)

        val addChipPanelLayout: LinearLayout = view.findViewById(R.id.web_layout_addpanel)
        //TODO: create a custom button
        val addChipCameraButton: ImageButton = view.findViewById(R.id.web_layout_addpanel_camera)
        val addChipFilesButton: ImageButton = view.findViewById(R.id.web_layout_addpanel_files)

        val upBranchButton: ImageButton = view.findViewById(R.id.web_layout_button_branchup)

        //Horizontal recycler view for "sibling" children
        val uRecyclerView = view.findViewById<RecyclerView>(R.id.web_layout_recycler_u_chips).apply {
            adapter = uRecyclerAdapter
            layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
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

        val chipObserver: Observer<List<ChipCard>> = Observer { chips ->

            uRecyclerAdapter.setChips(chips)
            //Clear the lower list
            lRecyclerAdapter.setChips(listOf())
        }

        localViewModel.getParent().observe(this, Observer { parent ->
            //Parent changed: remove old observer, replace with new one
            localViewModel.getListUpper().removeObserver(chipObserver)
            localViewModel.getListUpper().observe(this, chipObserver)

            if(parent.isTopic)
                upBranchButton.visibility = View.GONE
            else
                upBranchButton.visibility = View.VISIBLE
        })

        localViewModel.getListLower().observe(this, Observer { chips ->
            lRecyclerAdapter.setChips(chips)
        })

        localViewModel.prompts.observe(this, Observer { prompt ->

            val id = localViewModel.chipTouchId
            if(id == -1L)
                return@Observer

            when {
                prompt.selectChip -> {
                    val position = localViewModel.chipTouchPos

                    if(position == -1)
                        return@Observer

                    localViewModel.setListLower(id)

                    localViewModel.resetFlags()
                }

                prompt.toNextScreen -> {

                    val directions = WebFragmentDirections.actionWebFragmentToChipFragment(id)
                    findNavController().navigate(directions)

                    globalViewModel.saveChipFragParentId(localViewModel.getParentId())

                    localViewModel.resetFlags()
                }
            }
        })

        addChipCameraButton.setOnClickListener(this)
        addChipFilesButton.setOnClickListener(this)

        upBranchButton.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View?) {

        when(view?.id) {

            R.id.web_layout_button_branchup -> {
                localViewModel.navigateUpBranch()

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
                        localViewModel.saveChip("", imageFile.storagePath)
                }
            }
            R.id.web_layout_addpanel_files -> {

            }
        }
    }

    override fun topicTouch(position: Int, chipId: Long, event: MotionEvent, listType: Int) {
        gestureDetector.onTouchEvent(event)

        if(position == -1)
            return

        when(listType) {

            ListType.LOWER -> localViewModel.handleLowerChipsTouch(chipId)
            ListType.UPPER -> localViewModel.handleUpperChipsTouch(position, chipId)
        }
    }

    inner class ChipGestureDetector : GestureDetector.SimpleOnGestureListener() {

        //According to developer website, must return true to ensure gestures are not ignored
        override fun onDown(event: MotionEvent?): Boolean {
            localViewModel.setChipTouchGesture(DirectoryActivity.Constants.GESTURE_DOWN)

            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {
            localViewModel.setChipTouchGesture(DirectoryActivity.Constants.GESTURE_UP)

            return super.onSingleTapUp(event)
        }

        override fun onLongPress(event: MotionEvent?) {
            localViewModel.setChipTouchGesture(DirectoryActivity.Constants.GESTURE_LONG_TOUCH)
        }
    }
}