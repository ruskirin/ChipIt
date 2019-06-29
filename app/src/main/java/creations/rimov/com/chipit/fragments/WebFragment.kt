package creations.rimov.com.chipit.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.adapters.WebRecyclerAdapter
import creations.rimov.com.chipit.adapters.WebSpinnerAdapter
import creations.rimov.com.chipit.database.objects.Chip
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.view_models.WebViewModel
import creations.rimov.com.chipit.viewgroups.WebDetailLayout
import kotlinx.android.synthetic.main.web_detail_layout.*
import kotlinx.android.synthetic.main.web_detail_layout.view.*
import kotlinx.android.synthetic.main.web_layout.view.*

class WebFragment : Fragment(), WebRecyclerAdapter.WebAdapterHandler, View.OnTouchListener {

    //Passed Bundle from DirectoryFragment
    private val passedArgs by navArgs<WebFragmentArgs>()

    private lateinit var globalViewModel: GlobalViewModel
    private val localViewModel: WebViewModel by lazy {
        ViewModelProviders.of(this).get(WebViewModel::class.java)
    }

    private val parentAdapter: WebSpinnerAdapter by lazy {WebSpinnerAdapter()}
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

        if(localViewModel.getParentId() == -1L) {

            if(passedArgs.parentId != -1L) {
                val id = passedArgs.parentId

                localViewModel.setParent(id)

            } else {
                localViewModel.setParent(globalViewModel.getObservedChipId())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.web_layout, container, false)

        detailLayout = view.webParentView

        view.webDetailParentSpinner.adapter = parentAdapter

        view.webChildrenView.apply {
            adapter = childrenAdapter
            layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
            setHasFixedSize(true)
        }

        localViewModel.getParent().observe(this, Observer {

            globalViewModel.setObservedChipId(it.id) //Set focused chip id for editor chip creation

            detailLayout.setChip(it)
        })

        localViewModel.getParents().observe(this, Observer {
            parentAdapter.setParents(it)
        })

        localViewModel.getChildren().observe(this, Observer {
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

                    localViewModel.getParentAsChip()?.let {
                        globalViewModel.setChipToEdit(it)
                    }
                }
            }

//            R.id.detailEditorBtnDelete -> {
//                if(event.action == MotionEvent.ACTION_UP)
//
//                    localViewModel.getParentAsChip()?.let {
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
        globalViewModel.setChipToEdit(
            Chip(chip.id, isTopic = true, name = chip.name, desc = chip.desc,
                created = chip.dateCreate, counter = chip.counter))
    }

    override fun chipDelete(chip: ChipCard) {
        globalViewModel.deleteChip(chip.getChip(localViewModel.getParentId()))
    }

    private fun addSpinnerToToolbar() {

    }

    inner class ChipGestureDetector : GestureDetector.SimpleOnGestureListener() {

        //Must return true to ensure gestures are not ignored
        override fun onDown(event: MotionEvent?): Boolean {

            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {

            localViewModel.setParent(childrenAdapter.getSelectedId())

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