package creations.rimov.com.chipit.activities

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import creations.rimov.com.chipit.adapters.DirectoryRecyclerAdapter
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.Topic
import creations.rimov.com.chipit.util.handlers.RecyclerHandler
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.view_models.DirectoryViewModel
import java.io.IOException

class DirectoryActivity : AppCompatActivity(), View.OnClickListener, RecyclerHandler {

    private val directoryVm: DirectoryViewModel by lazy {
        ViewModelProviders.of(this).get(DirectoryViewModel::class.java)
    }

    //private val subjects = mutableListOf<Subject>()

    private lateinit var gestureDetector: GestureDetector

    private lateinit var directoryRecyclerView: RecyclerView
    private lateinit var directoryRecyclerAdapter: DirectoryRecyclerAdapter
    private lateinit var directoryLayoutManager: StaggeredGridLayoutManager

    private lateinit var addChipFab: FloatingActionButton
    private lateinit var addChipPanelLayout: LinearLayout
    private lateinit var addChipCameraButton: ImageButton       //TODO: create a custom button
    private lateinit var addChipFilesButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.directory_layout)

        gestureDetector = GestureDetector(this, TopicGestureDetector())
        gestureDetector.setIsLongpressEnabled(true)

        directoryRecyclerAdapter = DirectoryRecyclerAdapter(this, this)
        directoryLayoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        directoryRecyclerView = findViewById<RecyclerView>(R.id.directory_layout_recycler_main).apply {
            adapter = directoryRecyclerAdapter
            layoutManager = directoryLayoutManager
            setHasFixedSize(true)
        }

        addChipFab = findViewById(R.id.directory_layout_fab_add)
        addChipPanelLayout = findViewById(R.id.directory_layout_addpanel)
        addChipCameraButton = findViewById(R.id.directory_layout_addpanel_camera)
        addChipFilesButton = findViewById(R.id.directory_layout_addpanel_files)

        addChipFab.setOnClickListener(this)
        addChipCameraButton.setOnClickListener(this)
        addChipFilesButton.setOnClickListener(this)

        directoryVm.getTopics().observe(this, Observer {
            directoryRecyclerAdapter.setTopics(it)
        })
    }

    override fun onClick(view: View?) {

        when(view?.id) {

            R.id.directory_layout_fab_add -> {
                if(addChipPanelLayout.visibility == View.GONE) {
                    addChipPanelLayout.visibility = View.VISIBLE
                    addChipFab.hide()
                }
            }

            R.id.directory_layout_addpanel_camera -> {
                val addChipCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                //Verifies that an application that can handle this intent exists
                addChipCameraIntent.resolveActivity(packageManager)

                //TODO: handle error
                val imageFile = try {
                    CameraUtil.createImageFile(this)

                } catch(e: IOException) {
                    e.printStackTrace()
                    null
                }

                if(imageFile != null) {
                    val imageUri = FileProvider.getUriForFile(this,
                        CameraUtil.IMAGE_PROVIDER_AUTHORITY,
                        imageFile.file)

                    addChipCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(addChipCameraIntent, CameraUtil.CODE_TAKE_PICTURE)

                    //TODO: Change passed in values to variables
                    directoryVm.insertTopic(Topic(0, "DEFAULT", imageFile.storagePath))
                }
            }

            R.id.directory_layout_addpanel_files -> {

            }
        }
    }

    //TODO: find a way to handle this using MVVM pattern
    //Handle recyclerview's touch events
    override fun topicTouch(position: Int, event: MotionEvent, list: Int) {

        gestureDetector.onTouchEvent(event)

        if(directoryVm.topicPressed.value == true) {
            val toWeb = Intent(this, WebActivity::class.java)
            toWeb.putExtra("topic_id", directoryVm.getTopic(position)?.id)

            startActivity(toWeb)

            directoryVm.topicPressed.value = false
        }
    }

    inner class TopicGestureDetector : GestureDetector.SimpleOnGestureListener() {

        //According to developer website, must override onDown to return true to ensure gestures are not ignored
        override fun onDown(event: MotionEvent?): Boolean {

            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {

            directoryVm.handleTopicTouch(1)
            return super.onSingleTapUp(event)
        }

        override fun onLongPress(event: MotionEvent?) {

            directoryVm.handleTopicTouch(2)
        }
    }
}