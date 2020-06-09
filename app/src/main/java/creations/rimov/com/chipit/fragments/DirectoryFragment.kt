package creations.rimov.com.chipit.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.adapters.DirectoryRecyclerAdapter
import creations.rimov.com.chipit.constants.EditorConsts
import creations.rimov.com.chipit.extensions.getViewModel
import creations.rimov.com.chipit.extensions.nav
import creations.rimov.com.chipit.view_models.DirectoryViewModel
import creations.rimov.com.chipit.view_models.CommsViewModel
import kotlinx.android.synthetic.main.frag_directory.view.*

class DirectoryFragment
    : Fragment(),
      DirectoryRecyclerAdapter.DirectoryAdapterHandler {

    private lateinit var commsVM: CommsViewModel

    //Fragment's own ViewModel
    private val localVM: DirectoryViewModel by lazy {
        getViewModel<DirectoryViewModel>()
    }

    private lateinit var recyclerAdapter: DirectoryRecyclerAdapter

    private lateinit var gestureDetector: GestureDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("DirectoryFrag", "::onCreate()")

        activity?.let {
            commsVM = it.getViewModel()

            recyclerAdapter = DirectoryRecyclerAdapter(
                  this@DirectoryFragment)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(
          R.layout.frag_directory, container, false)

        view.dirRecycler.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
                setHasFixedSize(true)
        }

        gestureDetector = GestureDetector(activity, TopicGestureDetector())
        gestureDetector.setIsLongpressEnabled(true)

        localVM.getTopics().observe(viewLifecycleOwner, Observer { topics ->
            recyclerAdapter.setTopics(topics)
        })

        localVM.getChildren().observe(viewLifecycleOwner, Observer {children ->
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
        findNavController().nav(directions)
    }

    override fun topicEdit(chipId: Long) {

        //Necessary to set editAction here to avoid repeated, uncalled for actions
        commsVM.setEditAction(EditorConsts.EDIT)
        findNavController().nav(
          DirectoryFragmentDirections
              .actionDirectoryFragmentToEditorFragment(EditorConsts.EDIT, chipId))
    }

    override fun topicDelete(chipId: Long) {

        //NOT setting editAction for delete because it first has to be confirmed
        //  in the prompt; the prompt has the responsibility of setting action
        findNavController().nav(
          DirectoryFragmentDirections
              .actionDirectoryFragmentToEditorFragment(EditorConsts.DELETE, chipId)
        )
    }

    inner class TopicGestureDetector : GestureDetector.SimpleOnGestureListener() {

        //According to developer website, must override onDown to return true to ensure gestures are not ignored
        override fun onDown(event: MotionEvent?): Boolean {
            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {
            //Edit screen out, cannot click on the view till it's closed
            if(recyclerAdapter.selectedTopic.isEditing())
                return false

            recyclerAdapter.selectedTopic.topicChip.toggleDetail()

            if(recyclerAdapter.selectedTopic.isExpanded())
                localVM.setTopicChildren(recyclerAdapter.getSelectedId())

            return true
        }

        override fun onLongPress(event: MotionEvent?) {

            if(recyclerAdapter.selectedTopic.isEditing())
                recyclerAdapter.selectedTopic.edit(false)
            else
                recyclerAdapter.selectedTopic.edit(true)
        }
    }
}