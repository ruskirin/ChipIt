package creations.rimov.com.chipit.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.adapters.WebRecyclerAdapter
import creations.rimov.com.chipit.util.handlers.RecyclerHandler
import creations.rimov.com.chipit.objects.Subject
import creations.rimov.com.chipit.view_models.WebViewModel

class WebActivity : AppCompatActivity(), RecyclerHandler {

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

    private lateinit var gestureDetector: GestureDetector

    private val hChipList = mutableListOf<Subject>()
    private val vChipList = mutableListOf<Subject>()

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

        gestureDetector = GestureDetector(this, ChipGestureDetector())
        gestureDetector.setIsLongpressEnabled(true)

        webVm.getChipsHorizontal()?.observe(this, Observer {
            hWebRecyclerAdapter.setChips(it)
        })
    }

    override fun topicTouch(position: Int, event: MotionEvent, list: Int) {
        gestureDetector.onTouchEvent(event)

        if(chipPressed) {
            val toChip = Intent(this, ChipActivity::class.java)

            if(list == Constant.HORIZONTAL_CHIP_LIST) {
                toChip.putExtra("chip", hChipList[position])
                startActivity(toChip)

            } else if(list == Constant.VERTICAL_CHIP_LIST) {
                toChip.putExtra("chip", vChipList[position])
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
