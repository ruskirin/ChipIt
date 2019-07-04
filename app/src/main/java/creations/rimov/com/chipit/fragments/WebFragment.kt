package creations.rimov.com.chipit.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.activities.MainActivity
import creations.rimov.com.chipit.adapters.WebRecyclerAdapter
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.objects.ChipAction
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.view_models.WebViewModel
import creations.rimov.com.chipit.viewgroups.WebDetailLayout
import kotlinx.android.synthetic.main.web_layout.view.*

class WebFragment : Fragment(), WebRecyclerAdapter.WebAdapterHandler, View.OnTouchListener {

    //Passed Bundle from DirectoryFragment
    private val passedArgs by navArgs<WebFragmentArgs>()

    private lateinit var globalViewModel: GlobalViewModel
    private val localViewModel: WebViewModel by lazy {
        ViewModelProviders.of(this).get(WebViewModel::class.java)
    }

    private val childrenAdapter: WebRecyclerAdapter by lazy {WebRecyclerAdapter(this@WebFragment)}

    private lateinit var detailLayout: WebDetailLayout

    private lateinit var gestureDetector: GestureDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            globalViewModel = ViewModelProviders.of(it).get(GlobalViewModel::class.java)

            gestureDetector = GestureDetector(it, ChipGestureDetector())
            gestureDetector.setIsLongpressEnabled(true)
        }

        localViewModel.setChip(passedArgs.parentId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.web_layout, container, false)

        detailLayout = view.webParentView

        view.webChildrenView.apply {
            adapter = childrenAdapter
            layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
            setHasFixedSize(true)
        }

        localViewModel.getChip().observe(this, Observer {

            globalViewModel.observedChipId = it?.id //Set focused chip id for editor chip creation

            detailLayout.setChip(it)
        })
        //Parents to display in toolbar from MainActivity
        localViewModel.getParents().observe(this, Observer {
            globalViewModel.setWebParents(it)
        })

        localViewModel.getChildren().observe(this, Observer {
            Log.i("Life Event", "WebFragment#childrenObserver: ${it.size} children of chip")

            childrenAdapter.setChips(it)
        })

        detailLayout.setTouchListener(this)

        return view
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        if(event == null) return false

        when(view?.id) {

            R.id.webDetailBtnSettings -> {

                if(event.action == MotionEvent.ACTION_UP)
                    detailLayout.toggleEditor()
            }

            R.id.webDetailBtnDesc -> {

                if(detailLayout.isEditing()) return false

                if(event.action == MotionEvent.ACTION_UP)
                    detailLayout.toggleDesc()
            }

            R.id.detailEditorBtnEdit -> {

                if(event.action == MotionEvent.ACTION_UP) {

                    localViewModel.getAsChip()?.let {
                        globalViewModel.setChipAction(
                            ChipAction.instance(it, MainActivity.EditorAction.EDIT))
                    }
                }
            }

//            R.id.detailEditorBtnDelete -> {
//                if(event.action == MotionEvent.ACTION_UP)
//
//                    localViewModel.getAsChip()?.let {
//                        globalViewModel.deleteChip(it)
//                    }
//            }
        }

        return true
    }

    override fun chipTouch(event: MotionEvent) {
        gestureDetector.onTouchEvent(event)
    }

    override fun chipEdit(chip: ChipIdentity) {
        globalViewModel.setChipAction(
            ChipAction.instance(chip.getChip(), MainActivity.EditorAction.EDIT))
    }

    override fun chipDelete(chip: ChipCard) {
        globalViewModel.setChipAction(
            ChipAction.instance(chip.getChip(localViewModel.getChipId()), MainActivity.EditorAction.DELETE))
    }

    inner class ChipGestureDetector : GestureDetector.SimpleOnGestureListener() {

        //Must return true to ensure gestures are not ignored
        override fun onDown(event: MotionEvent?): Boolean {

            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {

            localViewModel.setChip(childrenAdapter.getSelectedId())

            return true
        }

        override fun onLongPress(event: MotionEvent?) {

            if(childrenAdapter.selectedChip.isEditing())
                childrenAdapter.selectedChip.edit(false)
            else
                childrenAdapter.selectedChip.edit(true)
        }
    }
}