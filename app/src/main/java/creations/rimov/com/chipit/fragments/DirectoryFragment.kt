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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.activities.DirectoryActivity
import creations.rimov.com.chipit.adapters.DirectoryRecyclerAdapter
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.view_models.DirectoryViewModel
import creations.rimov.com.chipit.view_models.GlobalViewModel
import java.io.IOException

class DirectoryFragment : Fragment(), DirectoryRecyclerAdapter.DirectoryAdapterHandler, View.OnClickListener {

    private lateinit var globalViewModel: GlobalViewModel

    //Fragment's own ViewModel
    private val localViewModel: DirectoryViewModel by lazy {
        ViewModelProviders.of(this).get(DirectoryViewModel::class.java)
    }

    private lateinit var dirRecyclerAdapter: DirectoryRecyclerAdapter

    private lateinit var gestureDetector: GestureDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("Life Event", "DirectoryFragment#onCreate()")

        activity?.let {
            globalViewModel = ViewModelProviders.of(it).get(GlobalViewModel::class.java)

            dirRecyclerAdapter = DirectoryRecyclerAdapter(it, this@DirectoryFragment)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.directory_content_layout, container, false)

        val dirRecyclerView: RecyclerView = view.findViewById<RecyclerView>(R.id.directory_layout_recycler_main)
            .apply {
                adapter = dirRecyclerAdapter
                layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
                setHasFixedSize(true)
        }

        gestureDetector = GestureDetector(activity, TopicGestureDetector())
        gestureDetector.setIsLongpressEnabled(true)

        val addChipPanelLayout: LinearLayout = view.findViewById(R.id.directory_layout_addpanel)
        val addChipCameraButton: ImageButton = view.findViewById(R.id.directory_layout_addpanel_camera)
        val addChipFilesButton: ImageButton = view.findViewById(R.id.directory_layout_addpanel_files)

        addChipCameraButton.setOnClickListener(this)
        addChipFilesButton.setOnClickListener(this)

        globalViewModel.getFabFlag().observe(this, Observer { flag ->

            if(flag.touched) {
                addChipPanelLayout.visibility = View.VISIBLE

            } else {
                addChipPanelLayout.visibility = View.GONE
            }
        })

        localViewModel.getTopics().observe(this, Observer { topics ->
            dirRecyclerAdapter.setTopics(topics)
        })

        localViewModel.prompts.observe(this, Observer { prompt ->
            val id = localViewModel.chipTouchId

            if(id == -1L)
                return@Observer

            when {
                prompt.toNextScreen -> {
                    Log.i("Touch Event", "DirectoryFragment#promptObserver: to next screen!")

                    val directions = DirectoryFragmentDirections.actionDirectoryFragmentToWebFragment(id)
                    findNavController().navigate(directions)
                }

                prompt.selectChip -> {
                    dirRecyclerAdapter.setEditVisibility(true)
                }
            }
        })

        return view
    }

    override fun onClick(view: View?) {

        when(view?.id) {

            R.id.directory_layout_addpanel_camera -> {
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

                    //TODO: Change passed in values to variables
                    localViewModel.insertTopic(Chip(0L, 0L, true, "TOPIC", imageFile.storagePath))
                }
            }

            R.id.directory_layout_addpanel_files -> {

            }
        }
    }

    //Handle recyclerview's touched events
    override fun topicTouch(id: Long, event: MotionEvent) {
        localViewModel.chipTouchId = id

        gestureDetector.onTouchEvent(event)
    }

    override fun topicEditImage(id: Long, event: MotionEvent) {

    }

    override fun topicEditDesc(id: Long, event: MotionEvent) {

    }

    override fun topicDelete(id: Long, event: MotionEvent) {

    }


    inner class TopicGestureDetector : GestureDetector.SimpleOnGestureListener() {

        //According to developer website, must override onDown to return true to ensure gestures are not ignored
        override fun onDown(event: MotionEvent?): Boolean {
            Log.i("Touch Event", "onDown()!")

            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {
            Log.i("Touch Event", "onSingleTapUp()!")

            localViewModel.handleChipGesture(DirectoryActivity.Constants.GESTURE_UP)

            return super.onSingleTapUp(event)
        }

        override fun onLongPress(event: MotionEvent?) {
            Log.i("Touch Event", "onLongPress()!")

            localViewModel.handleChipGesture(DirectoryActivity.Constants.GESTURE_LONG_TOUCH)
        }
    }
}