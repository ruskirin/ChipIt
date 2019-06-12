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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.activities.MainActivity
import creations.rimov.com.chipit.adapters.AlbumRecyclerAdapter
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.view_models.AlbumViewModel
import java.io.IOException

class AlbumFragment : Fragment(), View.OnClickListener, AlbumRecyclerAdapter.AlbumAdapterHandler {

    private lateinit var globalViewModel: GlobalViewModel

    private val localViewModel: AlbumViewModel by lazy {
        ViewModelProviders.of(this).get(AlbumViewModel::class.java)
    }

    //Passed Bundle from DirectoryFragment
    private val passedArgs by navArgs<AlbumFragmentArgs>()

    private lateinit var recyclerAdapter: AlbumRecyclerAdapter

    private lateinit var gestureDetector: GestureDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            globalViewModel = ViewModelProviders.of(it).get(GlobalViewModel::class.java)

            recyclerAdapter = AlbumRecyclerAdapter(it, this@AlbumFragment)

            gestureDetector = GestureDetector(it, ChipGestureDetector())
            gestureDetector.setIsLongpressEnabled(true)
        }

        Log.i("Life Event", "AlbumFragment#onCreate(): passed parent id: ${passedArgs.parentId}")

        if(localViewModel.getParentId() == -1L) {

            if(passedArgs.parentId != -1L)
                localViewModel.setParent(passedArgs.parentId)
            else
                localViewModel.setParent(globalViewModel.chipFragParentId)
        }

        globalViewModel.getUpFlag().observe(this, Observer { flag ->

            if(flag.touched)
                localViewModel.navigateUpBranch()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.album_layout, container, false)

        val addPanelLayout: LinearLayout = view.findViewById(R.id.album_button_layout)
        //TODO: create a custom button
        val addCameraButton: ImageButton = view.findViewById(R.id.album_button_camera)
        val addPhotosButton: ImageButton = view.findViewById(R.id.album_button_photos)

        //Horizontal recycler view for "sibling" children
        val recyclerView = view.findViewById<RecyclerView>(R.id.album_recycler).apply {
            adapter = recyclerAdapter
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            setHasFixedSize(true)
        }

        globalViewModel.getFabFlag().observe(this, Observer { flag ->

            if(flag.touched) {
                addPanelLayout.visibility = View.VISIBLE

            } else {
                addPanelLayout.visibility = View.GONE
            }
        })

        localViewModel.getChips().observe(this, Observer { chips ->
            recyclerAdapter.setChips(chips)
            //Clear the lower list

            val isTopic: Boolean = localViewModel.getParent().value?.isTopic ?: true
            globalViewModel.displayUp(!isTopic)
        })

        localViewModel.prompts.observe(this, Observer { prompt ->

            val id = localViewModel.chipTouchId
            if(id == -1L)
                return@Observer

            when {
                prompt.editChip -> {

                    recyclerAdapter.toggleEdit()
                }

                prompt.toNextScreen -> {
                    //No clicking through the edit screen
                    if(recyclerAdapter.isEditing())
                        return@Observer

                    val directions = AlbumFragmentDirections.actionAlbumFragmentToWebFragment(id)
                    findNavController().navigate(directions)

                    globalViewModel.saveChipFragParentId(localViewModel.getParentId())
                }
            }
        })

        addCameraButton.setOnClickListener(this)
        addPhotosButton.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View?) {

        when(view?.id) {

            R.id.album_button_camera -> {
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
                        localViewModel.saveChip("Lorem Ipsum", imageFile.storagePath)
                }
            }
            R.id.album_button_photos -> {

            }
        }
    }

    override fun topicTouch(id: Long, event: MotionEvent) {
        gestureDetector.onTouchEvent(event)

        localViewModel.handleChipTouch(id)
    }

    override fun topicExpand(id: Long) {
        localViewModel.setParent(id)
    }

    override fun topicDelete(id: Long) {
        localViewModel.deleteChip(id)
    }

    inner class ChipGestureDetector : GestureDetector.SimpleOnGestureListener() {

        //According to developer website, must return true to ensure gestures are not ignored
        override fun onDown(event: MotionEvent?): Boolean {
            Log.i("Touch Event", "Album.ChipGestureDetector#onDown()!")

            localViewModel.setChipTouchGesture(MainActivity.Constants.GESTURE_DOWN)

            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {
            Log.i("Touch Event", "Album.ChipGestureDetector#onUp()!")

            localViewModel.setChipTouchGesture(MainActivity.Constants.GESTURE_UP)

            return super.onSingleTapUp(event)
        }

        override fun onLongPress(event: MotionEvent?) {
            Log.i("Touch Event", "Album.ChipGestureDetector#onLongPress()!")

            localViewModel.setChipTouchGesture(MainActivity.Constants.GESTURE_LONG_TOUCH)
        }
    }
}