package creations.rimov.com.chipit.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.adapters.WebRecyclerAdapter
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.Topic
import creations.rimov.com.chipit.util.handlers.RecyclerHandler
import creations.rimov.com.chipit.objects.Subject
import creations.rimov.com.chipit.util.CameraUtil
import creations.rimov.com.chipit.view_models.WebViewModel
import java.io.IOException

class WebActivity : AppCompatActivity(), RecyclerHandler, View.OnClickListener {

    object Constant {
        const val HORIZONTAL_CHIP_LIST = 0
        const val VERTICAL_CHIP_LIST = 1
    }

    private val webVm: WebViewModel by lazy {
        ViewModelProviders.of(this).get(WebViewModel::class.java)
    }

    //Horizontal recycler view for "sibling" children
    private lateinit var hChipRecyclerView: RecyclerView
    private lateinit var hWebRecyclerAdapter: WebRecyclerAdapter
    private lateinit var hChipLayoutManager: LinearLayoutManager
    //Vertical recycler view for "children" children
    private lateinit var vChipRecyclerView: RecyclerView
    private lateinit var vWebRecyclerAdapter: WebRecyclerAdapter
    private lateinit var vChipLayoutManager: LinearLayoutManager

    private val addChipFab by lazy {
        findViewById<FloatingActionButton>(R.id.web_layout_fab_add)
    }
    private val addChipPanelLayout by lazy {
        findViewById<LinearLayout>(R.id.web_layout_addpanel)
    }
    //TODO: create a custom button
    private val addChipCameraButton by lazy {
        findViewById<ImageButton>(R.id.web_layout_addpanel_camera)
    }
    private val addChipFilesButton by lazy {
        findViewById<ImageButton>(R.id.web_layout_addpanel_camera)
    }

    private lateinit var gestureDetector: GestureDetector

    private var chipPressed = false
    private var chipLongPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_layout)

        //Intercept the passed object
        val parcel: Bundle? = intent.extras

        if(parcel != null) {
            val topicId: Long? = parcel.getLong("topic_id")

            if(topicId != null)
                webVm.initChips(topicId)
        }

        hWebRecyclerAdapter = WebRecyclerAdapter(this, Constant.HORIZONTAL_CHIP_LIST, this)
        hChipLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        hChipRecyclerView = findViewById<RecyclerView>(R.id.web_layout_recycler_h_chips).apply {
            adapter = hWebRecyclerAdapter
            layoutManager = hChipLayoutManager
            setHasFixedSize(true)
        }

        vWebRecyclerAdapter = WebRecyclerAdapter(this, Constant.VERTICAL_CHIP_LIST, this)
        vChipLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        vChipRecyclerView = findViewById<RecyclerView>(R.id.web_layout_recycler_v_chips).apply {
            adapter = vWebRecyclerAdapter
            layoutManager = vChipLayoutManager
            setHasFixedSize(true)
        }

        addChipFab.setOnClickListener(this)
        addChipCameraButton.setOnClickListener(this)
        addChipFilesButton.setOnClickListener(this)

        gestureDetector = GestureDetector(this, ChipGestureDetector())
        gestureDetector.setIsLongpressEnabled(true)

        webVm.getChipsHorizontal()?.observe(this, Observer {
            hWebRecyclerAdapter.setChips(it)
        })
    }

    override fun onClick(view: View?) {

        when(view?.id) {

            R.id.web_layout_fab_add -> {
                Log.i("TOUCH", "Fab")

                if(addChipPanelLayout.visibility == View.GONE) {
                    addChipPanelLayout.visibility = View.VISIBLE
                    addChipFab.hide()
                }
            }

            R.id.web_layout_addpanel_camera -> {
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
                    webVm.insertChipH(Chip(0, 0, "", imageFile.storagePath, null))
                }
            }

            R.id.web_layout_addpanel_files -> {

            }
        }
    }

    override fun topicTouch(position: Int, event: MotionEvent, list: Int) {
        gestureDetector.onTouchEvent(event)

        if(chipPressed) {
            val toChip = Intent(this, ChipActivity::class.java)

            //TODO (FUTURE): address this ugly mess of null checks
            if(list == Constant.HORIZONTAL_CHIP_LIST) {
                toChip.putExtra("chip_id", webVm.getChipsHorizontal()!!.value!![position].id)
                startActivity(toChip)

            } else if(list == Constant.VERTICAL_CHIP_LIST) {
                toChip.putExtra("chip_id", webVm.getChipsVertical()?.value?.get(position)?.id)
                startActivity(toChip)
            }

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
