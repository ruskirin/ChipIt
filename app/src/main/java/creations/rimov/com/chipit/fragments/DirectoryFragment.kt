package creations.rimov.com.chipit.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.activities.MainActivity
import creations.rimov.com.chipit.adapters.DirectoryRecyclerAdapter
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.view_models.DirectoryViewModel
import creations.rimov.com.chipit.view_models.GlobalViewModel
import kotlinx.android.synthetic.main.directory_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class DirectoryFragment : Fragment(), DirectoryRecyclerAdapter.DirectoryAdapterHandler {

    private lateinit var globalViewModel: GlobalViewModel

    //Fragment's own ViewModel
    private val localViewModel: DirectoryViewModel by lazy {
        ViewModelProviders.of(this).get(DirectoryViewModel::class.java)
    }

    private lateinit var recyclerAdapter: DirectoryRecyclerAdapter

    private lateinit var gestureDetector: GestureDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("Life Event", "DirectoryFragment#onCreate()")

        localViewModel.updateTopics()

        activity?.let {
            globalViewModel = ViewModelProviders.of(it).get(GlobalViewModel::class.java)

            recyclerAdapter = DirectoryRecyclerAdapter(it, this@DirectoryFragment)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.directory_layout, container, false)

        view.dirRecycler.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
                setHasFixedSize(true)
        }

        gestureDetector = GestureDetector(activity, TopicGestureDetector())
        gestureDetector.setIsLongpressEnabled(true)

        localViewModel.getTopics().observe(this, Observer { topics ->

            recyclerAdapter.setTopics(topics)
        })

//        localViewModel.prompts.observe(this, Observer { prompt ->
//            val id = recyclerAdapter.getSelectedId()
//
//            if(id == -1L)
//                return@Observer
//
//            when {
//                prompt.toNextScreen -> {
//                    //Edit screen out, cannot click on the view till it's closed
//                    if(recyclerAdapter.isEditing())
//                        return@Observer
//
//                    val directions = DirectoryFragmentDirections.actionDirectoryFragmentToAlbumFragment(id)
//                    findNavController().navigate(directions)
//                }
//
//                prompt.editChip -> {
//
//                    recyclerAdapter.toggleEditing()
//                }
//            }
//        })

        return view
    }

    //Handle recyclerview's touched events
    override fun topicTouch(id: Long, event: MotionEvent) {

        gestureDetector.onTouchEvent(event)
    }

    override fun topicEditImage(id: Long, event: MotionEvent) {
        TODO()
    }

    override fun topicEditDesc(id: Long, text: String) {
        TODO()
    }

    override fun topicDelete(id: Long) {
        TODO()
    }

    inner class TopicGestureDetector : GestureDetector.SimpleOnGestureListener() {

        //According to developer website, must override onDown to return true to ensure gestures are not ignored
        override fun onDown(event: MotionEvent?): Boolean {
            Log.i("Touch Event", "onDown()!")

            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {
            Log.i("Touch Event", "onSingleTapUp()!")

            localViewModel.handleChipGesture(MainActivity.Constants.GESTURE_UP)

            return true
        }

        override fun onLongPress(event: MotionEvent?) {
            Log.i("Touch Event", "onLongPress()!")

            localViewModel.handleChipGesture(MainActivity.Constants.GESTURE_LONG_TOUCH)
        }
    }
}