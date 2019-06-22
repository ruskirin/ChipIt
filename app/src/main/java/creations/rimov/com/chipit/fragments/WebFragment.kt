package creations.rimov.com.chipit.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.activities.MainActivity
import creations.rimov.com.chipit.adapters.WebRecyclerAdapter
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.view_models.WebViewModel
import kotlinx.android.synthetic.main.web_layout.view.*

class WebFragment : Fragment(), WebRecyclerAdapter.AlbumAdapterHandler {

    private lateinit var globalViewModel: GlobalViewModel

    private val localViewModel: WebViewModel by lazy {
        ViewModelProviders.of(this).get(WebViewModel::class.java)
    }

    //Passed Bundle from DirectoryFragment
    private val passedArgs by navArgs<AlbumFragmentArgs>()

    private lateinit var recyclerAdapter: WebRecyclerAdapter

    private lateinit var gestureDetector: GestureDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            globalViewModel = ViewModelProviders.of(it).get(GlobalViewModel::class.java)

            recyclerAdapter = WebRecyclerAdapter(it, this@WebFragment)

            gestureDetector = GestureDetector(it, ChipGestureDetector())
            gestureDetector.setIsLongpressEnabled(true)
        }

        Log.i("Life Event", "WebFragment#onCreate(): passed parent id: ${passedArgs.parentId}")

        if(localViewModel.getParentId() == -1L) {

            if(passedArgs.parentId != -1L)
                localViewModel.setParent(passedArgs.parentId)
            else
                localViewModel.setParent(globalViewModel.chipFragParentId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.web_layout, container, false)

        //Horizontal recycler view for "sibling" children
        view.webRecycler.apply {
            adapter = recyclerAdapter
            layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
            setHasFixedSize(true)
        }

        localViewModel.getChips().observe(this, Observer { chips ->
            recyclerAdapter.setChips(chips)

            val isTopic: Boolean = localViewModel.getParent().value?.isTopic ?: true

            localViewModel.getParent().value?.let {
                globalViewModel.setAlbumChip(it)
            }
        })

        localViewModel.prompts.observe(this, Observer { prompt ->

            val id = recyclerAdapter.getSelectedId()

            if(id == -1L)
                return@Observer

            when {
                prompt.toNextScreen -> {
                    //No clicking through the edit screen
                    if(recyclerAdapter.isEditing())
                        return@Observer

                    val directions = AlbumFragmentDirections.actionAlbumFragmentToWebFragment(id)
                    findNavController().navigate(directions)

                    globalViewModel.chipFragParentId = localViewModel.getParentId()
                }

                prompt.editChip -> {

                    recyclerAdapter.toggleEditing()
                }
            }
        })

        return view
    }

    override fun topicTouch(id: Long, event: MotionEvent) {
        gestureDetector.onTouchEvent(event)
    }

    override fun topicExpand(id: Long) {
        localViewModel.setParent(id)
    }

    override fun topicDelete(id: Long) {
        TODO()
    }

    inner class ChipGestureDetector : GestureDetector.SimpleOnGestureListener() {

        //According to developer website, must return true to ensure gestures are not ignored
        override fun onDown(event: MotionEvent?): Boolean {

            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {
            Log.i("Touch Event", "Album.ChipGestureDetector#onUp()!")

            localViewModel.handleChipGesture(MainActivity.Constants.GESTURE_UP)

            return true
        }

        override fun onLongPress(event: MotionEvent?) {
            Log.i("Touch Event", "Album.ChipGestureDetector#onLongPress()!")

            localViewModel.handleChipGesture(MainActivity.Constants.GESTURE_LONG_TOUCH)
        }
    }
}