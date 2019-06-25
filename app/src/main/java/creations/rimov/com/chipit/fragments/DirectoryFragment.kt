package creations.rimov.com.chipit.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.adapters.DirectoryRecyclerAdapter
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipTopic
import creations.rimov.com.chipit.view_models.DirectoryViewModel
import creations.rimov.com.chipit.view_models.GlobalViewModel
import kotlinx.android.synthetic.main.directory_layout.view.*

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

        activity?.let {
            globalViewModel = ViewModelProviders.of(it).get(GlobalViewModel::class.java)

            recyclerAdapter = DirectoryRecyclerAdapter(this@DirectoryFragment)
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

        localViewModel.getChildren().observe(this, Observer { children ->
            recyclerAdapter.setChildren(children)
        })

        return view
    }

    //Handle recyclerview's touched events
    override fun topicTouch(event: MotionEvent) {

        gestureDetector.onTouchEvent(event)
    }

    override fun topicToWeb() {

        val directions =
            DirectoryFragmentDirections.actionDirectoryFragmentToWebFragment(recyclerAdapter.getSelectedId())
        findNavController().navigate(directions)
    }

    override fun topicEdit(chip: ChipTopic) {

        globalViewModel.setChipToEdit(
            Chip(chip.id, isTopic = true, name = chip.name, desc = chip.desc,
                created = chip.dateCreate, updated = chip.dateUpdate, counter = chip.counter))
    }

    override fun topicDelete(chip: ChipTopic) {
        globalViewModel.deleteChip(chip.id, null, chip.counter)
    }

    inner class TopicGestureDetector : GestureDetector.SimpleOnGestureListener() {

        //According to developer website, must override onDown to return true to ensure gestures are not ignored
        override fun onDown(event: MotionEvent?): Boolean {
            Log.i("Touch Event", "onDown()!")

            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {
            Log.i("Touch Event", "onSingleTapUp()!")

            //Edit screen out, cannot click on the view till it's closed
            if(recyclerAdapter.selectedTopic.isEditing())
                return false

            recyclerAdapter.selectedTopic.topicChip.toggleDetail()

            if(recyclerAdapter.selectedTopic.isExpanded())
                localViewModel.setTopicChildren(recyclerAdapter.getSelectedId())

            return true
        }

        override fun onLongPress(event: MotionEvent?) {
            Log.i("Touch Event", "onLongPress()!")

            if(recyclerAdapter.selectedTopic.isEditing())
                recyclerAdapter.selectedTopic.edit(false)
            else
                recyclerAdapter.selectedTopic.edit(true)
        }
    }
}