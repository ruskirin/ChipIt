package creations.rimov.com.chipit.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
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
import creations.rimov.com.chipit.database.objects.ChipCard
import creations.rimov.com.chipit.database.objects.ChipIdentity
import creations.rimov.com.chipit.objects.ChipAction
import creations.rimov.com.chipit.view_models.GlobalViewModel
import creations.rimov.com.chipit.view_models.WebViewModel
import kotlinx.android.synthetic.main.web_layout.*
import kotlinx.android.synthetic.main.web_layout.view.*
import kotlinx.android.synthetic.main.web_layout.view.webChildrenLayout

class WebFragment : Fragment(),
    WebRecyclerAdapter.WebAdapterHandler, View.OnTouchListener /*MotionLayout.TransitionListener*/ {

    //Passed Bundle from DirectoryFragment
    private val passedArgs by navArgs<WebFragmentArgs>()

    private lateinit var globalViewModel: GlobalViewModel
    private val localViewModel: WebViewModel by lazy {
        ViewModelProviders.of(this).get(WebViewModel::class.java)
    }

    private val childrenAdapter: WebRecyclerAdapter by lazy {WebRecyclerAdapter(this@WebFragment)}

    private lateinit var gestureDetector: GestureDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            globalViewModel = ViewModelProviders.of(it).get(GlobalViewModel::class.java)

            gestureDetector = GestureDetector(it, ChipGestureDetector())
            gestureDetector.setIsLongpressEnabled(true)
        }

        localViewModel.setFocusId(passedArgs.parentId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.web_layout, container, false)

        view.webChildrenView.apply {
            adapter = childrenAdapter
            layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
            setHasFixedSize(true)
        }

        localViewModel.getChip().observe(this, Observer {

            globalViewModel.observedChipId = it?.id //Set focused chip id for editor chip creation
        })
        //Parents to display in toolbar from MainActivity
        localViewModel.getParents().observe(this, Observer {
            globalViewModel.setWebParents(it)
        })

        localViewModel.getChildren().observe(this, Observer {
            childrenAdapter.setChips(it)
        })

        globalViewModel.getWebSelectedId().observe(this, Observer {
            localViewModel.setFocusId(it)
        })

        return view
    }

//    override fun onDetach() {
//        super.onDetach()
//        //Reset the layoutparams
//        if(params.behavior == null) params.behavior = layoutBehavior
//    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        if(event == null) return false

        when(view?.id) {

//            R.id.webDetailBtnDesc -> {
//
//                if(event.action == MotionEvent.ACTION_UP)
//                    toggleDesc()
//            }

            R.id.webDetailBtnChip -> {

                if(event.action == MotionEvent.ACTION_UP) {
                    val directions =
                        WebFragmentDirections.actionWebFragmentToChipperFragment(localViewModel.getChipId() ?: return false)
                    findNavController().navigate(directions)
                }
            }

            R.id.webDetailBtnSettings -> {

                if(event.action == MotionEvent.ACTION_UP) {

                    localViewModel.getAsChip()?.let {
                        globalViewModel.setChipEdit(
                            ChipAction.instance(it, MainActivity.EditorAction.EDIT))
                    }
                }
            }

            R.id.webDetailBtnDelete -> {

                if(event.action == MotionEvent.ACTION_UP)

                    localViewModel.getAsChip()?.let {
                        globalViewModel.setChipEdit(
                            ChipAction.instance(it, MainActivity.EditorAction.DELETE))
                    }
            }
        }

        return true
    }

    //RecyclerAdapter Handler ---------------------------------------------------------
    override fun chipTouch(event: MotionEvent) {
        gestureDetector.onTouchEvent(event)
    }

    override fun chipEdit(chip: ChipIdentity) {
        globalViewModel.setChipEdit(
            ChipAction.instance(chip.getChip(), MainActivity.EditorAction.EDIT))
    }

    override fun chipDelete(chip: ChipCard) {
        globalViewModel.setChipEdit(
            ChipAction.instance(chip.getChip(localViewModel.getChipId()), MainActivity.EditorAction.DELETE))
    }
    //---------------------------------------------------------------------------------

    inner class ChipGestureDetector : GestureDetector.SimpleOnGestureListener() {

        //Must return true to ensure gestures are not ignored
        override fun onDown(event: MotionEvent?): Boolean {

            return true
        }

        override fun onSingleTapUp(event: MotionEvent?): Boolean {

            localViewModel.setFocusId(childrenAdapter.getSelectedId())

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