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
import creations.rimov.com.chipit.adapters.DirectoryRecyclerAdapter
import creations.rimov.com.chipit.database.objects.Topic
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.util.handlers.RecyclerHandler
import creations.rimov.com.chipit.view_models.DirectoryViewModel
import creations.rimov.com.chipit.view_models.GlobalViewModel
import java.io.IOException

class DirectoryFragment : Fragment(), RecyclerHandler, View.OnClickListener {

    private lateinit var globalViewModel: GlobalViewModel

    //Fragment's own ViewModel
    private val dirViewModel: DirectoryViewModel by lazy {
        ViewModelProviders.of(this).get(DirectoryViewModel::class.java)
    }

    private lateinit var dirRecyclerView: RecyclerView
    private lateinit var dirRecyclerAdapter: DirectoryRecyclerAdapter

    private lateinit var gestureDetector: GestureDetector

    private lateinit var addChipPanelLayout: LinearLayout
    private lateinit var addChipCameraButton: ImageButton       //TODO: create a custom button
    private lateinit var addChipFilesButton: ImageButton

    private var touchTopicId: Long = -1

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

        dirRecyclerView = view.findViewById<RecyclerView>(R.id.directory_layout_recycler_main)
            .apply {
                adapter = dirRecyclerAdapter
                layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
                setHasFixedSize(true)
        }

        gestureDetector = GestureDetector(activity, TopicGestureDetector())
        gestureDetector.setIsLongpressEnabled(true)

        addChipPanelLayout = view.findViewById(R.id.directory_layout_addpanel)
        addChipCameraButton = view.findViewById(R.id.directory_layout_addpanel_camera)
        addChipFilesButton = view.findViewById(R.id.directory_layout_addpanel_files)

        addChipCameraButton.setOnClickListener(this)
        addChipFilesButton.setOnClickListener(this)

        globalViewModel.fabTouched.observe(this, Observer { touched ->

            if(touched) {
                addChipPanelLayout.visibility = View.VISIBLE

            } else {
                addChipPanelLayout.visibility = View.GONE
            }
        })

        dirViewModel.getTopics().observe(this, Observer { topics ->
            dirRecyclerAdapter.setTopics(topics)
        })

        dirViewModel.topicTouch.observe(this, Observer { flag ->

            if(touchTopicId == -1L)
                return@Observer

            when {
                flag.chipTouched -> {
                    val directions = DirectoryFragmentDirections.actionDirectoryFragmentToWebFragment(touchTopicId)
                    findNavController().navigate(directions)
                }

                flag.chipLongTouched -> {
                    //TODO FUTURE: implement long touch feature
                }
            }
        })

        return view
    }

    //Handle recyclerview's touch events
    override fun topicTouch(position: Int, event: MotionEvent, listType: Int) {

        gestureDetector.onTouchEvent(event)

        touchTopicId = dirViewModel.getTopic(position)?.id ?: -1L
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
                    dirViewModel.insertTopic(Topic(0, "DEFAULT", imageFile.storagePath))
                }
            }

            R.id.directory_layout_addpanel_files -> {

            }
        }
    }

    inner class TopicGestureDetector : GestureDetector.SimpleOnGestureListener() {

        //According to developer website, must override onDown to return true to ensure gestures are not ignored
        override fun onDown(event: MotionEvent?): Boolean {
            Log.i("Touch Event", "onDown()!")

            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {
            Log.i("Touch Event", "onSingleTapUp()!")

            dirViewModel.setTopicTouch()

            return super.onSingleTapUp(event)
        }

        override fun onLongPress(event: MotionEvent?) {
            Log.i("Touch Event", "onLongPress()!")

            dirViewModel.setTopicLongTouch()
        }
    }
}